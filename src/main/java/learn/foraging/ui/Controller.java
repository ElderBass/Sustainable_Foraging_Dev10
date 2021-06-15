package learn.foraging.ui;

import learn.foraging.data.DataException;
import learn.foraging.domain.ForageService;
import learn.foraging.domain.ForagerService;
import learn.foraging.domain.ItemService;
import learn.foraging.domain.Result;
import learn.foraging.models.Category;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import learn.foraging.models.Item;

import javax.xml.crypto.Data;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Controller {

    private final ForagerService foragerService;
    private final ForageService forageService;
    private final ItemService itemService;
    private final View view;

    public Controller(ForagerService foragerService, ForageService forageService, ItemService itemService, View view) {
        this.foragerService = foragerService;
        this.forageService = forageService;
        this.itemService = itemService;
        this.view = view;
    }

    public void run() {
        view.displayHeader("Welcome to Sustainable Foraging");
        try {
            runAppLoop();
        } catch (DataException ex) {
            view.displayException(ex);
        }
        view.displayHeader("Goodbye.");
    }

    private void runAppLoop() throws DataException {
        MainMenuOption option;
        do {
            option = view.selectMainMenuOption();
            switch (option) {
                case VIEW_FORAGES_BY_DATE:
                    viewByDate();
                    break;
                case VIEW_ITEMS:
                    viewItems();
                    break;
                case VIEW_FORAGERS:
                    viewForagers();
                    break;
                case ADD_FORAGE:
                    addForage();
                    break;
                case ADD_FORAGER:
                    addForager();
                    break;
                case UPDATE_FORAGER:
                    updateForager();
                    break;
                case ADD_ITEM:
                    addItem();
                    break;
                case REPORT_KG_PER_ITEM:
                    viewTotalKilogramsPerItem();
                    break;
                case REPORT_CATEGORY_VALUE:
                    viewTotalValuePerCategory();
                    break;
                case GENERATE:
                    generate();
                    break;
            }
        } while (option != MainMenuOption.EXIT);
    }

    // top level menu
    private void viewByDate() {
        view.displayHeader(MainMenuOption.VIEW_FORAGES_BY_DATE.getMessage());
        LocalDate date = view.getForageDate();
        List<Forage> forages = forageService.findByDate(date);
        view.displayForages(forages);
        view.enterToContinue();
    }

    private void viewTotalKilogramsPerItem() throws DataException {
        LocalDate date = view.getForageDate();
        Map<String, Double> itemMap = forageService.findKilogramsOfItemsOnDate(date);
        if (itemMap == null || itemMap.isEmpty()) {
            System.out.println("Proceeding to Main Menu");
            return;
        }
        view.displayKilosPerItem(itemMap);
        view.enterToContinue();
    }

    private void viewTotalValuePerCategory() throws DataException {
        view.displayHeader(MainMenuOption.REPORT_CATEGORY_VALUE.getMessage());
        LocalDate date = view.getForageDate();
        Map<String, Double> categories = forageService.findTotalValueOfCategory(date);
        if (categories == null || categories.isEmpty()) {
            System.out.println("Proceeding to Main Menu");
            return;
        }
        view.displayCategoryValues(categories);
        view.enterToContinue();
    }

    private void viewForagers() {
        view.displayHeader(MainMenuOption.VIEW_FORAGERS.getMessage());
        int filter = view.chooseForagerFilter();
        List<Forager> foragers = filterForagers(filter);

        if (foragers == null || foragers.isEmpty()) {
            System.out.println("Proceeding to Main Menu");
            return;
        } else {
            view.displayForagers(foragers);
            view.enterToContinue();
        }
    }

    private void viewItems() {
        view.displayHeader(MainMenuOption.VIEW_ITEMS.getMessage());
        Category category = view.getItemCategory();
        List<Item> items = itemService.findByCategory(category);
        view.displayHeader("Items");
        view.displayItems(items);
        view.enterToContinue();
    }

    private void addForage() throws DataException {
        view.displayHeader(MainMenuOption.ADD_FORAGE.getMessage());
        Forager forager = getForager();
        if (forager == null) {
            return;
        }
        Item item = getItem();
        if (item == null) {
            return;
        }
        Forage forage = view.makeForage(forager, item);
        Result<Forage> result = forageService.add(forage);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Forage %s created.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
    }

    private void updateForage() throws DataException {
        // how to select a Forage?
    }


    private void addForager() throws DataException {
        Forager forager = view.makeForager();
        Result<Forager> result = foragerService.addForager(forager);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Forager %s created.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
    }

    public void updateForager() throws DataException {
        view.displayHeader("Update a Forager");
        String lastNamePrefix = view.getForagerNamePrefix();
        System.out.println();
        List<Forager> foragers = foragerService.findByLastName(lastNamePrefix);
        Forager forager = view.chooseForager(foragers);
        Forager updated = view.updateForager(forager);
        Result<Forager> result = foragerService.updateForager(updated);
        if (result.isSuccess()) {
            view.displayStatus(true, "Forager " + updated.getId() + " has been updated.");
        } else {
            view.displayStatus(false, result.getErrorMessages());
        }
    }


    private void addItem() throws DataException {
        Item item = view.makeItem();
        Result<Item> result = itemService.add(item);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Item %s created.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
    }

    private void generate() throws DataException {
        GenerateRequest request = view.getGenerateRequest();
        if (request != null) {
            int count = forageService.generate(request.getStart(), request.getEnd(), request.getCount());
            view.displayStatus(true, String.format("%s forages generated.", count));
        }
    }

    // support methods
    private Forager getForager() {
        String lastNamePrefix = view.getForagerNamePrefix();
        List<Forager> foragers = foragerService.findByLastName(lastNamePrefix);
        return view.chooseForager(foragers);
    }

    private Item getItem() {
        Category category = view.getItemCategory();
        List<Item> items = itemService.findByCategory(category);
        return view.chooseItem(items);
    }

    private List<Forager> filterForagers(int filter) {
        List<Forager> foragers = null;
        switch (filter) {
            case 0:
                view.displayHeader("Viewing All Foragers");
                System.out.println();
                foragers = foragerService.findAll();
                break;
            case 1:
                foragers = filterForagersByLastName();
                break;
            case 2:
                foragers = filterForagersByState();
                break;
            case 3:
                foragers = filterForagersByDate();
                break;
        }
        return foragers;
    }

    private List<Forager> filterForagersByLastName() {
        String lastNamePrefix = view.getForagerNamePrefix();
        System.out.println();
        view.displayHeader("Viewing Forager Results For \"" + lastNamePrefix + "\"");
        List<Forager> foragers = foragerService.findByLastName(lastNamePrefix);
        return foragers;
    }

    private List<Forager> filterForagersByState() {
        String state = view.getForagerState();
        System.out.println();
        Result<List<Forager>> result = foragerService.findByState(state);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
            return null;
        } else {
            view.displayHeader("Viewing Foragers From " + state.toUpperCase());
            return result.getPayload();
        }
    }

    private List<Forager> filterForagersByDate() {
        LocalDate date = view.getForageDate();
        System.out.println();
        List<Forage> forages = forageService.findByDate(date);
        List<Forager> foragers = new ArrayList<>();
        if (forages == null || forages.isEmpty()) {
            return foragers;
        }
        view.displayHeader("Viewing Foragers on " + date);
        for (Forage f : forages) {
            foragers.add(f.getForager());
        }
        foragers = foragers.stream()
                .sorted(Comparator.comparing(Forager::getLastName))
                .collect(Collectors.toList());
        return foragers;
    }
}
