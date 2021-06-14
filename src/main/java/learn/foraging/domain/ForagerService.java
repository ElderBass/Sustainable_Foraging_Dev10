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

    public List<Forager> findAll() {
        return repository.findAll();
    }

    public Result<List<Forager>> findByState(String stateAbbr) {
        List<Forager> foragers = repository.findByState(stateAbbr);
        Result<List<Forager>> result = validateList(foragers);
        return result;
    }

    public List<Forager> findByLastName(String prefix) {
        return repository.findAll().stream()
                .filter(i -> i.getLastName().startsWith(prefix))
                .collect(Collectors.toList());
    }


    public Result<Forager> addForager(Forager forager) throws DataException {
        Result result = validate(forager);
        if (!result.isSuccess()) {
            return result;
        }

        result.setPayload(repository.addForager(forager));

        return result;
    }

    public Result<Forager> updateForager(Forager forager) throws DataException {
        Result result = validate(forager);
        if (!result.isSuccess()) {
            return result;
        }

        if (repository.updateForager(forager)) {
            result.setPayload(forager);
            System.out.println("Forager " + forager.getId() + " Has Been Updated.");
        }  else {
            result.addErrorMessage("Could Not Find Forager With ID " + forager.getId());
        }
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

    private Result<List<Forager>> validateList(List<Forager> foragers) {
        Result<List<Forager>> result = new Result<>();
        if (foragers.isEmpty() || foragers == null) {
            result.addErrorMessage("No Foragers Found. Please Try Again.");
        } else {
            result.setPayload(foragers);
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

        if (forager.getState().length() > 2) {
            result.addErrorMessage("State must be abbreviated.");
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
