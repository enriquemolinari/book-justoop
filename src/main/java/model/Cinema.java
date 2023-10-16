package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import model.api.CinemaSystem;
import model.api.CreditCardPaymentGateway;
import model.api.DetailedMovieInfo;
import model.api.EmailProvider;
import model.api.Genre;
import model.api.MovieInfo;
import model.api.MovieShows;
import model.api.ShowInfo;
import model.api.Ticket;

public class Cinema implements CinemaSystem {
	static final String MOVIE_ID_DOES_NOT_EXISTS = "Movie ID not found";
	static final String SHOW_TIME_ID_NOT_EXISTS = "Show ID not found";
	static final String USER_ID_NOT_EXISTS = "User not registered";
	static final String CREDIT_CARD_DEBIT_HAS_FAILED = "Credit card debit have failed";
	private EntityManagerFactory emf;
	private CreditCardPaymentGateway paymentGateway;
	private EmailProvider emailProvider;
	private EntityManager em;

	public Cinema(EntityManagerFactory emf,
			CreditCardPaymentGateway paymentGateway,
			EmailProvider emailProvider) {
		this.emf = emf;
		this.paymentGateway = paymentGateway;
		this.emailProvider = emailProvider;
	}

	public List<MovieShows> showsUntil(LocalDateTime untilTo) {
		return inTx(em -> {
			return movieShowsUntil(untilTo);
		});
	}

	private List<MovieShows> movieShowsUntil(LocalDateTime untilTo) {
		var query = em.createQuery(
				"from Movie m join fetch m.showTimes s join fetch s.screenedIn "
						+ "where s.startTime >= ?1 and s.startTime <= ?2 "
						+ "order by m.name asc",
				Movie.class).setParameter(1, LocalDateTime.now())
				.setParameter(2, untilTo);

		return query.getResultList().stream()
				.map(movieShow -> movieShow.toMovieShow())
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public List<MovieInfo> moviesSortedByName() {
		return inTx(em -> {
			var query = em.createQuery(
					"from Movie m join fetch m.actors "
							+ "join fetch m.actors.person order by m.name asc",
					Movie.class);

			var list = query.getResultList();
			return list.stream().map(m -> m.toInfo()).toList();
		});
	}

	@Override
	public DetailedMovieInfo movie(Long id) {
		return inTx(em -> {
			try {
				return detailedMovieInfo(id);
			} catch (NonUniqueResultException | NoResultException e) {
				throw new BusinessException(MOVIE_ID_DOES_NOT_EXISTS);
			}
		});
	}

	private DetailedMovieInfo detailedMovieInfo(Long id) {
		return em
				.createQuery("from Movie m "
						+ "join fetch m.directorNames join fetch m.actors "
						+ "join fetch m.actors.person "
						+ "join fetch m.userRates ur join fetch ur.user u "
						+ "join fetch u.person " + "where m.id = ?1 "
						+ "order by m.name asc", Movie.class)
				.setParameter(1, id).getSingleResult().toDetailedInfo();
	}

	@Override
	public MovieInfo addNewMovie(String name, int duration,
			LocalDate releaseDate, String plot, Set<Genre> genres) {
		return inTx(em -> {
			var movie = new Movie(name, plot, duration, releaseDate, genres);
			em.persist(movie);
			return movie.toInfo();
		});
	}

	@Override
	public MovieInfo addActorToMovie(Long movieId, String name, String surname,
			String email, String characterName) {
		return inTx(em -> {
			var movie = em.getReference(Movie.class, movieId);
			movie.addAnActor(name, surname, email, characterName);
			return movie.toInfo();
		});
	}

	@Override
	public MovieInfo addDirectorToMovie(Long movieId, String name,
			String surname, String email) {
		return inTx(em -> {
			var movie = em.getReference(Movie.class, movieId);
			movie.addADirector(name, surname, email);
			return movie.toInfo();
		});
	}

	public ShowInfo addNewShowToMovie(Long movieId, LocalDateTime startTime,
			float price, Long theaterId, int pointsToWin) {
		return inTx(em -> {

			var movie = movieBy(movieId);
			var theatre = theatreBy(theaterId);

			var showTime = new ShowTime(movie, startTime, price, theatre,
					pointsToWin);

			em.persist(showTime);

			return showTime.toShowInfo();
		});
	}

	@Override
	public Ticket pay(Long userId, Long showTimeId, Set<Integer> selectedSeats,
			String creditCardNumber, YearMonth expirationDate,
			String secturityCode) {

		return inTx(em -> {
			ShowTime showTime = showTimeBy(showTimeId);
			var user = userBy(userId);

			var totalAmount = confirmSeatsAndGetTotalAmount(selectedSeats,
					showTime, user);

			tryCreditCardDebit(creditCardNumber, expirationDate, secturityCode,
					totalAmount);

			sendNewSaleEmailToTheUser(selectedSeats, showTime, user,
					totalAmount);

			var sale = new Sale(totalAmount, user, showTime,
					showTime.pointsToEarn());

			return sale.ticket();
		});

	}

	private void tryCreditCardDebit(String creditCardNumber,
			YearMonth expirationDate, String secturityCode, float totalAmount) {
		try {
			this.paymentGateway.pay(creditCardNumber, expirationDate,
					secturityCode, totalAmount);
		} catch (Exception e) {
			throw new BusinessException(CREDIT_CARD_DEBIT_HAS_FAILED, e);
		}
	}

	private void sendNewSaleEmailToTheUser(Set<Integer> selectedSeats,
			ShowTime showTime, User user, float totalAmount) {
		var emailTemplate = new NewSaleEmailTemplate(totalAmount,
				user.userName(), selectedSeats, showTime.movieName(),
				showTime.startDateTime());

		this.emailProvider.send(user.email(), emailTemplate.subject(),
				emailTemplate.body());
	}

	private float confirmSeatsAndGetTotalAmount(Set<Integer> selectedSeats,
			ShowTime showTime, User user) {
		showTime.confirmSeatsForUser(user, selectedSeats);
		var totalAmount = showTime.totalAmountForTheseSeats(selectedSeats);
		return totalAmount;
	}

	private Theater theatreBy(Long theatreId) {
		try {
			return em.getReference(Theater.class, theatreId);
		} catch (IllegalArgumentException e) {
			throw new BusinessException(MOVIE_ID_DOES_NOT_EXISTS);
		}
	}

	// TODO: refactor to remove duplicated code
	private Movie movieBy(Long movieId) {
		var movie = em.find(Movie.class, movieId);
		if (movie == null) {
			throw new BusinessException(MOVIE_ID_DOES_NOT_EXISTS);
		}
		return movie;
	}

	private User userBy(Long userId) {
		var user = em.find(User.class, userId);
		if (user == null) {
			throw new BusinessException(USER_ID_NOT_EXISTS);
		}

		return user;
	}

	private ShowTime showTimeBy(Long id) {
		var show = em.find(ShowTime.class, id);
		if (show == null) {
			throw new BusinessException(SHOW_TIME_ID_NOT_EXISTS);
		}

		return show;
	}

	private <T> T inTx(Function<EntityManager, T> s) {
		em = emf.createEntityManager();
		var tx = em.getTransaction();

		try {
			tx.begin();

			T t = s.apply(em);
			tx.commit();

			return t;
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			em.close();
		}
	}
}
