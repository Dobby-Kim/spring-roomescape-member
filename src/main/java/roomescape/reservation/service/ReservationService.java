package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.exception.IllegalReservationDateTimeRequestException;
import roomescape.exception.SaveDuplicateContentException;
import roomescape.reservation.dao.ReservationDao;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationTimeAvailabilityResponse;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;
import roomescape.time.dao.TimeDao;
import roomescape.time.domain.Time;

@Service
public class ReservationService {

    private final ReservationDao reservationDao;
    private final TimeDao timeDao;
    private final ThemeDao themeDao;

    public ReservationService(ReservationDao reservationDao, TimeDao timeDao, ThemeDao themeDao) {
        this.reservationDao = reservationDao;
        this.timeDao = timeDao;
        this.themeDao = themeDao;
    }

    public ReservationResponse addReservation(ReservationRequest reservationRequest) {
        Time time = timeDao.findById(reservationRequest.timeId());
        Theme theme = themeDao.findById(reservationRequest.themeId());
        validateReservationRequest(reservationRequest, time);
        Reservation reservation = reservationRequest.toReservation(time, theme);
        Reservation savedReservation = reservationDao.save(reservation);
        return ReservationResponse.fromReservation(savedReservation);
    }

    private void validateReservationRequest(ReservationRequest reservationRequest, Time time) {
        if (reservationRequest.date().isBefore(LocalDate.now())) {
            throw new IllegalReservationDateTimeRequestException("지난 날짜의 예약을 시도하였습니다.");
        }
        validateDuplicateReservation(reservationRequest, time);
    }

    private void validateDuplicateReservation(ReservationRequest reservationRequest, Time time) {
        List<Time> bookedTimes = getBookedTimesOfThemeAtDate(reservationRequest.themeId(), reservationRequest.date());
        if (isTimeBooked(time, bookedTimes)) {
            throw new SaveDuplicateContentException("해당 시간에 예약이 존재합니다.");
        }
    }

    public List<ReservationResponse> findReservations() {
        List<Reservation> reservations = reservationDao.findAllOrderByDateAndTime();

        return reservations.stream()
                .map(ReservationResponse::fromReservation)
                .toList();
    }

    public List<ReservationTimeAvailabilityResponse> findTimeAvailability(long themeId, LocalDate date) {
        List<Time> allTimes = timeDao.findAllReservationTimesInOrder();
        List<Time> bookedTimes = getBookedTimesOfThemeAtDate(themeId, date);

        return allTimes.stream()
                .map(time -> ReservationTimeAvailabilityResponse.fromTime(time, isTimeBooked(time, bookedTimes)))
                .toList();
    }

    private List<Time> getBookedTimesOfThemeAtDate(long themeId, LocalDate date) {
        List<Reservation> reservationsOfThemeInDate = reservationDao.findAllByThemeIdAndDate(themeId, date);
        return extractReservationTimes(reservationsOfThemeInDate);
    }

    private List<Time> extractReservationTimes(List<Reservation> reservations) {
        return reservations.stream()
                .map(Reservation::getReservationTime)
                .toList();
    }

    private boolean isTimeBooked(Time time, List<Time> bookedTimes) {
        return bookedTimes.contains(time);
    }

    public void removeReservations(long reservationId) {
        reservationDao.deleteById(reservationId);
    }

}
