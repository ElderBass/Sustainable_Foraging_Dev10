package learn.foraging.data;

import learn.foraging.models.Forager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ForagerFileRepositoryTest {

    private final ForagerRepository repo = new ForagerFileRepository("./data/foragers.csv");

    @Test
    void shouldFindAll() {
        List<Forager> all = repo.findAll();
        assertEquals(1000, all.size());
    }

    @Test
    void shouldAddForager() throws DataException {

        Forager newForager = new Forager();

        newForager.setFirstName("Test");
        newForager.setLastName("Testerson");
        newForager.setState("AL");
        Forager forager = repo.addForager(newForager);

        List<Forager> all = repo.findAll();
        assertEquals(1004, all.size());

        assertEquals("Test", repo.findById(forager.getId()).getFirstName());
    }
}