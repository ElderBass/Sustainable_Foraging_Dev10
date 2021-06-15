package learn.foraging.data;

import learn.foraging.models.Forage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ForageRepository {
    List<Forage> findByDate(LocalDate date);

    Forage add(Forage forage) throws DataException;

    boolean update(Forage forage) throws DataException;

    Map<String, Double> findKilogramsOfItemsOnDate(List<Forage> forages) throws DataException;

    Map<String, Double> findTotalValueOfCategory(List<Forage> forages) throws DataException;
}
