package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.ForagerRepository;
import learn.foraging.models.Forager;

import java.util.List;
import java.util.stream.Collectors;

public class ForagerService {

    private final ForagerRepository repository;

    public ForagerService(ForagerRepository repository) {
        this.repository = repository;
    }

    public List<Forager> findByState(String stateAbbr) {
        return repository.findByState(stateAbbr);
    }

    public List<Forager> findByLastName(String prefix) {
        return repository.findAll().stream()
                .filter(i -> i.getLastName().startsWith(prefix))
                .collect(Collectors.toList());
    }

    // TODO add an addForager() method and a validate() method for checking addForager input
    public Result<Forager> addForager(Forager forager) throws DataException {
        Result result = validate(forager);
        if (!result.isSuccess()) {
            return result;
        }

        result.setPayload(repository.addForager(forager));

        return result;
    }

    private Result<Forager> validate(Forager forager) {
        Result<Forager> result = validateNullAndEmpty(forager);
        if (!result.isSuccess()) {
            return result;
        }

        result = validateDuplicate(forager);
        if (!result.isSuccess()) {
            return result;
        }

        return result;
    }

    private Result<Forager> validateNullAndEmpty(Forager forager) {
        Result<Forager> result = new Result();

        if (forager == null) {
            result.addErrorMessage("Forager cannot be null.");
            return result;
        }

        if (forager.getFirstName() == null || forager.getFirstName().equals("")) {
            result.addErrorMessage("Forager first name is required.");
        }

        if (forager.getLastName() == null || forager.getLastName().equals("")) {
            result.addErrorMessage("Forager last name is required.");
        }

        if (forager.getState() == null || forager.getState().equals("")) {
            result.addErrorMessage("Forager state is required.");
        }

        return result;
    }

    private Result<Forager> validateDuplicate(Forager forager) {
        Result<Forager> result = new Result<>();
        List<Forager> all = repository.findAll();
        for (Forager f : all) {
            if (f.getState().equals(forager.getState()) && f.getLastName().equals(forager.getLastName()) && f.getFirstName().equals(forager.getFirstName())) {
                result.addErrorMessage("Forager already exists in that state. Cannot add duplicate foragers.");
            }
        }
        return result;
    }
}
