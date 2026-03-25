package app.data.scripts;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CardGameController implements Initializable {
    private static final int CARD_IMAGE_SIZE = 35;
    private static final int SKILL_IMAGE_SIZE = 35;
    private static final int MECHANIC_IMAGE_SIZE = 35;
    private static final int INFO_IMAGE_SIZE = 35;

    @FXML private HBox craftingArea, skillBar, cardBar;
    @FXML private VBox recipeMechanics, skillInfoArea;
    @FXML private Button craftButton;
    @FXML private Button startGame;

    public enum CardType { A, B }
    public enum SkillType {
        hit("attack.png", 5),
        block("block.png", 5),
        parry("counter.png", 3),
        criticalHit("heavy.png", 3),
        blockHeal("heal.png", 3),
        heldparry("longblock.png", 2),
        parryBurst("aoe.png", 3);

        final String imageFile;
        final int outputAmount;
        SkillType(String image, int amount) {
            this.imageFile = image;
            this.outputAmount = amount;
        }
    }

    private final Map<CardType, Integer> cards = new EnumMap<>(CardType.class);
    private final Map<SkillType, Integer> skills = new EnumMap<>(SkillType.class);
    private final List<CardType> craftingSlots = new ArrayList<>();

    private final List<Recipe> recipes = Arrays.asList(
            new Recipe(List.of(CardType.A), SkillType.hit),
            new Recipe(List.of(CardType.B), SkillType.block),
            new Recipe(List.of(CardType.A, CardType.B), SkillType.parry),
            new Recipe(List.of(CardType.A, CardType.A), SkillType.criticalHit),
            new Recipe(List.of(CardType.B, CardType.B, CardType.B), SkillType.blockHeal),
            new Recipe(List.of(CardType.B, CardType.B), SkillType.heldparry),
            new Recipe(List.of(CardType.B, CardType.B, CardType.A, CardType.A, CardType.A), SkillType.parryBurst)
    );

    public void setInitialCounts(
            int cardACount,
            int cardBCount,
            int normalAttackCount,
            int normalBlockCount,
            int counterAttackCount,
            int heavyAttackCount,
            int blockHealCount,
            int longBlockCount,
            int aoeAfterBlockCount) {

        cards.put(CardType.A, cardACount);
        cards.put(CardType.B, cardBCount);

        skills.put(SkillType.hit, normalAttackCount);
        skills.put(SkillType.block, normalBlockCount);
        skills.put(SkillType.parry, counterAttackCount);
        skills.put(SkillType.criticalHit, heavyAttackCount);
        skills.put(SkillType.blockHeal, blockHealCount);
        skills.put(SkillType.heldparry, longBlockCount);
        skills.put(SkillType.parryBurst, aoeAfterBlockCount);

        setupCardBar();
        setupSkillBar();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeUI();
        craftButton.setOnAction(e -> handleCraft());
    }

    private void initializeUI() {
        setupRecipeMechanics();
        setupSkillInfo();
        setupCardBar();
        setupSkillBar();
    }

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResource("/app/data/images/card/" + path).toExternalForm());
        } catch (Exception e) {
            System.err.println("圖勒");
            return null;
        }
    }

    private Image createPlaceholderImage(String text) {
        Rectangle rect = new Rectangle(CARD_IMAGE_SIZE, CARD_IMAGE_SIZE);
        rect.setFill(Color.LIGHTGRAY);
        rect.setStroke(Color.BLACK);

        StackPane pane = new StackPane(rect);
        Text textNode = new Text(text);
        textNode.setFont(Font.font(14));
        StackPane.setAlignment(textNode, Pos.CENTER);

        return pane.snapshot(null, null);
    }

    private StackPane createCardView(CardType type, int count) {
        ImageView imageView = new ImageView();
        try {
            String imagePath = type == CardType.A ? "card_a.png" : "card_b.png";
            imageView.setImage(loadImage(imagePath));
        } catch (Exception e) {
            imageView.setImage(createPlaceholderImage(type.name()));
        }
        imageView.setFitWidth(CARD_IMAGE_SIZE);
        imageView.setFitHeight(CARD_IMAGE_SIZE);
        return createCountedView(imageView, count);
    }

    private StackPane createSkillView(SkillType skill, int count) {
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(loadImage(skill.imageFile));
        } catch (Exception e) {
            imageView.setImage(createPlaceholderImage(skill.name().substring(0, 2)));
        }
        imageView.setFitWidth(SKILL_IMAGE_SIZE);
        imageView.setFitHeight(SKILL_IMAGE_SIZE);

        if (count > 0) {
            return createCountedView(imageView, count);
        }
        return new StackPane(imageView);
    }

    private StackPane createCountedView(ImageView imageView, int count) {
        StackPane stackPane = new StackPane(imageView);
        Label countLabel = new Label(String.valueOf(count));
        countLabel.setStyle("-fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.7);");
        StackPane.setAlignment(countLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(countLabel, new Insets(0, 5, 5, 0));
        stackPane.getChildren().add(countLabel);

        if (count == 0) {
            countLabel.setStyle("-fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.5); -fx-text-fill: #666;");
        }

        return stackPane;
    }

    private void setupRecipeMechanics() {
        recipeMechanics.getChildren().clear();

        for (Recipe recipe : recipes) {
            HBox hbox = new HBox(3);  
            hbox.setAlignment(Pos.CENTER_LEFT);

            Map<CardType, Long> requirements = recipe.getCardCounts();
            boolean first = true;
            for (Map.Entry<CardType, Long> entry : requirements.entrySet()) {
                if (!first) {
                    Label plusLabel = new Label("+");
                    plusLabel.setStyle("-fx-text-fill: white;");  
                    hbox.getChildren().add(plusLabel);
                }
                first = false;

                StackPane cardView = createCardView(entry.getKey(), entry.getValue().intValue());
                cardView.setMaxSize(MECHANIC_IMAGE_SIZE, MECHANIC_IMAGE_SIZE);
                hbox.getChildren().add(cardView);
            }

            StackPane skillView = createSkillView(recipe.result, recipe.result.outputAmount);
            skillView.setMaxSize(MECHANIC_IMAGE_SIZE, MECHANIC_IMAGE_SIZE);

            Label equalsLabel = new Label("=");
            equalsLabel.setStyle("-fx-text-fill: white;");
            hbox.getChildren().addAll(equalsLabel, skillView);
            recipeMechanics.getChildren().add(hbox);
        }
    }

    private void setupSkillInfo() {
        skillInfoArea.getChildren().clear();
        for (SkillType skill : SkillType.values()) {
            HBox hbox = new HBox(10);

            ImageView skillImage = new ImageView();
            try {
                skillImage.setImage(loadImage(skill.imageFile));
            } catch (Exception e) {
                skillImage.setImage(createPlaceholderImage(skill.name().substring(0, 2)));
            }
            skillImage.setFitWidth(INFO_IMAGE_SIZE);
            skillImage.setFitHeight(INFO_IMAGE_SIZE);

            Label skillLabel = new Label(skill.name());
            skillLabel.setStyle("-fx-text-fill: white;");  
            hbox.getChildren().addAll(skillImage, skillLabel);
            skillInfoArea.getChildren().add(hbox);
        }
    }


    private void setupCardBar() {
        cardBar.getChildren().clear();
        for (CardType type : CardType.values()) {
            int count = cards.getOrDefault(type, 0);
            StackPane cardView = createCardView(type, count);
            cardView.setOnMouseClicked(e -> {
                if (count > 0) {
                    moveToCraftingArea(type);
                }
            });
            if (count == 0) {
                cardView.setOpacity(0.6);
            }
            cardBar.getChildren().add(cardView);
        }
    }

    private void setupSkillBar() {
        skillBar.getChildren().clear();
        for (SkillType skill : SkillType.values()) {
            int count = skills.getOrDefault(skill, 0);
            StackPane skillView = createSkillView(skill, count);
            if (count == 0) {
                skillView.setOpacity(0.6);
            }
            skillBar.getChildren().add(skillView);
        }
    }

    private void moveToCraftingArea(CardType type) {
        if (cards.get(type) > 0) {
            cards.put(type, cards.get(type) - 1);
            craftingSlots.add(type);
            updateCraftingArea();
            setupCardBar();
        }
    }

    private void updateCraftingArea() {
        craftingArea.getChildren().clear();

        Map<CardType, Long> current = craftingSlots.stream()
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        current.forEach((type, count) -> {
            StackPane cardView = createCardView(type, count.intValue());
            cardView.setMaxSize(CARD_IMAGE_SIZE, CARD_IMAGE_SIZE);
            cardView.setOnMouseClicked(e -> returnToCardBar(type));
            craftingArea.getChildren().add(cardView);
        });
    }

    private void returnToCardBar(CardType type) {
        cards.put(type, cards.get(type) + 1);
        craftingSlots.remove(type);
        updateCraftingArea();
        setupCardBar();
    }

    private void handleCraft() {
        Optional<Recipe> matched = recipes.stream()
                .filter(r -> r.matches(craftingSlots))
                .findFirst();

        if (matched.isPresent()) {
            SkillType skill = matched.get().result;
            skills.put(skill, skills.get(skill) + skill.outputAmount);
            GameInfo.cardsUsed += craftingSlots.size();
            craftingSlots.clear();
            updateCraftingArea();
            setupSkillBar();
        }
    }

    /*private void showSkillInfo(SkillType skill) {
        System.out.println("選中技能: " + skill.name());
    }*/

    private static class Recipe {
        final List<CardType> requirements;
        final SkillType result;

        Recipe(List<CardType> req, SkillType res) {
            this.requirements = new ArrayList<>(req);
            Collections.sort(this.requirements);
            this.result = res;
        }

        boolean matches(List<CardType> cards) {
            if (cards == null) return false;

            List<CardType> sorted = new ArrayList<>(cards);
            Collections.sort(sorted);
            return sorted.equals(requirements);
        }

        Map<CardType, Long> getCardCounts() {
            return requirements.stream()
                    .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        }
    }

    @FXML
    private void handleStartGame() throws IOException {
        try {
            while (!craftingSlots.isEmpty()) {
                CardType type = craftingSlots.remove(0);
                cards.put(type, cards.get(type) + 1);
            }
            Stage stage = (Stage)startGame.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/data/scripts/Game.fxml"));
            Parent root = loader.load();
            Game gameController = loader.getController();
            gameController.receive(
                cards.get(CardType.A),
                cards.get(CardType.B),
                skills.get(SkillType.hit),
                skills.get(SkillType.criticalHit),
                skills.get(SkillType.block),
                skills.get(SkillType.blockHeal),
                skills.get(SkillType.parry),
                skills.get(SkillType.heldparry),
                skills.get(SkillType.parryBurst)
            );
            stage.setScene(new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT));
        }catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}