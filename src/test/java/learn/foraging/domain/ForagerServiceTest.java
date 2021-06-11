package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.ForagerRepository;
import learn.foraging.data.ForagerRepositoryDouble;
import learn.foraging.models.Forager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForagerServiceTest {

    ForagerService service = new ForagerService(new ForagerRepositoryDouble());

    @Test
    void shouldNotAddNull() throws DataException {
        Result<Forager> result = service.addForager(null);
        assertEquals("Forager cannot be null.", result.getErrorMessages().get(0));
    }

    @Test
    void shouldNotAddEmptyFirstName() throws DataException {
        Forager forager = new Forager();
        forager.setFirstName(null);
        forager.setLastName("Test");
        forager.setState("Test State");

        Result<Forager> result = service.addForager(forager);
        assertEquals("Forager first name is required.", result.getErrorMessages().get(0));

        forager.setFirstName("");

        result = service.addForager(forager);
        assertEquals("Forager first name is required.", result.getErrorMessages().get(0));

    }

    @Test
    void shouldNotAddEmptyLastName() throws DataException {
        Forager forager = new Forager();
        forager.setFirstName("Test");
        forager.setLastName(null);
        forager.setState("Test State");

        Result<Forager> result = service.addForager(forager);
        assertEquals("Forager last name is required.", result.getErrorMessages().get(0));

        forager.setLastName("");

        result = service.addForager(forager);
        assertEquals("Forager last name is required.", result.getErrorMessages().get(0));
    }

    @Test
    void shouldNotAddEmptyState() throws DataException {
        Forager forager = new Forager();
        forager.setFirstName("Test");
        forager.setLastName("Testerson");
        forager.setState(null);

        Result<Forager> result = service.addForager(forager);
        assertEquals("Forager state is required.", result.getErrorMessages().get(0));

        forager.setState("");

        result = service.addForager(forager);
        assertEquals("Forager state is required.", result.getErrorMessages().get(0));

    }

    @Test
    void shouldNotAddDuplicateForager() throws DataException {
        Forager forager = new Forager();
        forager.setId("0e4707f4-407e-4ec9-9665-baca0aabe88c");
        forager.setFirstName("Jilly");
        forager.setLastName("Sisse");
        forager.setState("GA");

        Result<Forager> result = service.addForager(forager);
        assertEquals("Forager already exists in that state. Cannot add duplicate foragers.", result.getErrorMessages().get(0));
    }

    @Test
    void shouldAdd() throws DataException {
        Forager forager = new Forager();
        forager.setId("0e4707f4-407e-4ec9-9665-bcad0aabe88c");
        forager.setFirstName("Deez");
        forager.setLastName("Nutz");
        forager.setState("GA");

        Result<Forager> result = service.addForager(forager);
        assertTrue(result.isSuccess());
    }
}