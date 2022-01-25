package bot;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CarFine extends TelegramLongPollingBot implements CarFineInterface{
    String state = "";

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }
    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageFromUser = update.getMessage().getText();
            switch (messageFromUser) {
                case "/start" -> {
                    state = "/start";
                }
                case "Search user" -> {
                    state = "Search user";
                }
                case "Fine history" -> {
                    state = "Fine history";
                }
                case "Search by number" -> {
                    state = "Search by number";
                }
                case "Search by passport" -> {
                    state = "Search by passport";
                }
            }
            executeState(state, chatId, messageFromUser);

        }
    }
    @SneakyThrows
    public void executeState(String state, String chatId, String messageFromUser) {
        switch (state) {
            case "/start" -> {
                SendMessage sendMessage = new SendMessage(chatId, "Welcome to fine checker bot");
                sendMessage.setReplyMarkup(menu());
                execute(sendMessage);
            }
            case "Search user" -> {
                SendMessage sendMessage = new SendMessage(chatId, "Enter user passport number");
                execute(sendMessage);
                this.state = "user_searching";
            }
            case "Fine history" -> {
                SendMessage sendMessage = new SendMessage(chatId, "Enter user passport number");
                this.state = "user_fine_history";
                execute(sendMessage);
            }
            case "Search by number" -> {
                SendMessage sendMessage = new SendMessage(chatId, "Enter user car number");
                this.state = "searching_fine_by_number";
                execute(sendMessage);
            }
            case "Search by passport" -> {
                SendMessage sendMessage = new SendMessage(chatId, "Enter user passport number");
                this.state = "searching_fine_by_passport";
                execute(sendMessage);
            }
            case "user_searching" -> {
                System.out.println(messageFromUser);
                Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/car_fine_bot", "postgres", "star");
                PreparedStatement preparedStatement = connection.prepareStatement("select * from get_user_info(?)");
                preparedStatement.setString(1, messageFromUser);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String stringBuilder = "name: " + resultSet.getString("name") + "\n" +
                            "password_number: " + resultSet.getString("passport_number") + "\n" +
                            "car name: " + resultSet.getString("car_name") + "\n" +
                            "car number: " + resultSet.getString("car_number");
                    SendMessage sendMessage = new SendMessage(chatId, stringBuilder);
                    execute(sendMessage);
                }
                connection.close();

            }
            case "user_fine_history" -> {

            }
            case "searching_fine_by_number" -> {
                System.out.println(" ");
            }
            case "searching_fine_by_passport" -> {
                System.out.println("s");
            }
            default -> {
                SendMessage sendMessage = new SendMessage(chatId, "something");
                execute(sendMessage);
            }
        }
    }
    public ReplyKeyboardMarkup menu() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("Search user");
        keyboardRow.add("Fine history");
        keyboardRowList.add(keyboardRow);

        keyboardRow = new KeyboardRow();
        keyboardRow.add("Search by number");
        keyboardRow.add("Search by passport");
        keyboardRowList.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        return replyKeyboardMarkup;
    }
}
