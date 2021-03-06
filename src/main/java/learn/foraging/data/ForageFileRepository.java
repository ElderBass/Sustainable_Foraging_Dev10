package learn.foraging.data;

import learn.foraging.models.Category;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import learn.foraging.models.Item;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ForageFileRepository implements ForageRepository {

    private static final String HEADER = "id,forager_id,item_id,kg";
    private final String directory;
   // private final ForagerRepository foragerRepository = new ForagerFileRepository("./data/foragers.csv");
   // private final ItemRepository itemRepository = new ItemFileRepository("./data/items.txt");

    public ForageFileRepository(String directory) {
        this.directory = directory;
    }

    @Override
    public List<Forage> findByDate(LocalDate date) {

        ArrayList<Forage> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(getFilePath(date)))) {

            reader.readLine(); // read header

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {

                String[] fields = line.split(",", -1);
                if (fields.length == 4) {
                    result.add(deserialize(fields, date));
                }
            }
        } catch (IOException ex) {
            // don't throw on read
        }
        return result;
    }

    @Override
    public Forage add(Forage forage) throws DataException {
        List<Forage> all = findByDate(forage.getDate());
        forage.setId(java.util.UUID.randomUUID().toString());
        all.add(forage);
        writeAll(all, forage.getDate());
        return forage;
    }

    @Override
    public boolean update(Forage forage) throws DataException {
        List<Forage> all = findByDate(forage.getDate());
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(forage.getId())) {
                all.set(i, forage);
                writeAll(all, forage.getDate());
                return true;
            }
        }
        return false;
    }

    private String getFilePath(LocalDate date) {
        return Paths.get(directory, date + ".csv").toString();
    }

    private void writeAll(List<Forage> forages, LocalDate date) throws DataException {
        try (PrintWriter writer = new PrintWriter(getFilePath(date))) {

            writer.println(HEADER);

            for (Forage item : forages) {
                writer.println(serialize(item));
            }
        } catch (FileNotFoundException ex) {
            throw new DataException(ex);
        }
    }

    private String serialize(Forage item) {
        return String.format("%s,%s,%s,%s",
                item.getId(),
                item.getForager().getId(),
                item.getItem().getId(),
                item.getKilograms());
    }

    private Forage deserialize(String[] fields, LocalDate date) {
        Forage result = new Forage();
        result.setId(fields[0]);
        result.setDate(date);
        result.setKilograms(Double.parseDouble(fields[3]));

        Forager forager = new Forager();
        forager.setId(fields[1]);
        result.setForager(forager);

        Item item = new Item();
        item.setId(Integer.parseInt(fields[2]));
        result.setItem(item);
        return result;
    }

    // TODO not sure if here or where but eventually need to ensure there are forages on that date and if not add an error message
    public Map<String, Double> findKilogramsOfItemsOnDate(List<Forage> forages) {

        Map<String, Double> itemMap = forages.stream().collect(Collectors.groupingBy(Forage::getItemName, Collectors.summingDouble(Forage::getKilograms)));
        return itemMap;
    }

    public Map<String, Double> findTotalValueOfCategory(List<Forage> forages) throws DataException {

        Double edibleValue = filterForageByCategory(forages, Category.EDIBLE);
        Double medicinalValue = filterForageByCategory(forages, Category.MEDICINAL);
        Double inedibleValue = filterForageByCategory(forages, Category.INEDIBLE);
        Double poisonValue = filterForageByCategory(forages, Category.POISONOUS);

        Map<String, Double> categories = new HashMap<>();
        categories.put("Edible", edibleValue);
        categories.put("Medicinal", medicinalValue);
        categories.put("Inedible", inedibleValue);
        categories.put("Poisonous", poisonValue);

        return categories;
    }

    private Double filterForageByCategory(List<Forage> forages, Category category) {
        Double categoryValue = forages.stream().filter(forage -> forage.getItem().getCategory() == category)
                .collect(Collectors.summingDouble(value -> value.getValue().doubleValue()));
        return categoryValue;
    }
}
