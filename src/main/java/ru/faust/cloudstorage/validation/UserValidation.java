package ru.faust.cloudstorage.validation;

import lombok.experimental.UtilityClass;
import ru.faust.cloudstorage.dto.UserDTO;
import ru.faust.cloudstorage.exception.InvalidParameterException;
import ru.faust.cloudstorage.exception.UtilityFilesLoadingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@UtilityClass
public class UserValidation {

    private static final Set<String> BAD_WORDS = new HashSet<>();

    static {
        try {
            loadDictionary();
        } catch (IOException e) {
            throw new UtilityFilesLoadingException("An error occurred while trying to load profanity dictionary. Please contact the developer.");
        }
    }

    private void loadDictionary() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(UserValidation.class.getClassLoader().getResourceAsStream("profanity/dictionary.en"))))) {
            String line;
            while ((line = br.readLine()) != null) {
                BAD_WORDS.add(line.toLowerCase());
            }
        }
    }

    public void validate(UserDTO user) {
        String maybeBadWord = containsBadWords(user.username());
        if (!maybeBadWord.isEmpty()) {
            throw new InvalidParameterException("Username contains prohibited words: " + maybeBadWord, "register");
        }
    }

    private String containsBadWords(String name) {
        String lowerCaseName = name.toLowerCase();
        for (String badWord : BAD_WORDS) {
            if (lowerCaseName.equals(badWord) || lowerCaseName.contains(badWord)) {
                return badWord;
            }
        }
        return "";
    }
}
