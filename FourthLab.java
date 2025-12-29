import java.util.ArrayList;
import java.util.List;

public class FourthLab {

    public static void main(String[] args) {
        try {
            // Вхідні дані
            int studentId = 11;
            int wordLength = 4;

            // Початковий текст
            String inputText =
                    "Java    — це  потужна\tмова  для   створення  клас  програм. " +
                            "Цей тест  код  добре  показує,  як   працює   композиція   класів!";

            System.out.println("Student ID = " + studentId);
            System.out.println("\nПочатковий текст:");
            System.out.println(inputText);

            // Парсинг тексту у створені класи

            Text text = Text.parseFromString(inputText);

            // Нормалізація пробілів і табуляцій
            text.normalizeSpacesAndTabs();

            text.removeWords(wordLength);

            System.out.println("\nЗмінений текст:");
            System.out.println(text);

        } catch (Exception e) {
            System.out.println("Помилка виконання програми: " + e.getMessage());
        }
    }
}

class Text {

    private final List<Sentence> sentences;

    private Text(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public static Text parseFromString(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Текст не може бути null.");
        }

        List<Sentence> result = new ArrayList<>();
        List<TextToken> currentTokens = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            // Пробіли та табуляції завершують слово
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                flushWordIfNeeded(currentWord, currentTokens);
                continue;
            }

            // Кінець речення
            if (isSentenceTerminator(ch)) {
                flushWordIfNeeded(currentWord, currentTokens);
                currentTokens.add(new Punctuation(ch));

                if (!currentTokens.isEmpty()) {
                    result.add(new Sentence(currentTokens));
                }
                currentTokens = new ArrayList<>();
                continue;
            }

            // Розділові знаки всередині речення
            if (isInnerPunctuation(ch)) {
                flushWordIfNeeded(currentWord, currentTokens);
                currentTokens.add(new Punctuation(ch));
                continue;
            }

            // Символ є частиною слова
            currentWord.append(ch);
        }

        // Додаємо залишкове слово або речення
        flushWordIfNeeded(currentWord, currentTokens);
        if (!currentTokens.isEmpty()) {
            result.add(new Sentence(currentTokens));
        }

        return new Text(result);
    }


    public void normalizeSpacesAndTabs() {
        for (Sentence s : sentences) {
            s.normalize();
        }
    }

    public void removeWords(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Довжина слова повинна бути додатною.");
        }
        for (Sentence s : sentences) {
            s.removeWords(length);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Sentence s : sentences) {
            sb.append(s).append(" ");
        }
        return sb.toString().trim();
    }

    // Допоміжний метод для додавання слова у список токенів
    private static void flushWordIfNeeded(
            StringBuilder currentWord,
            List<TextToken> tokens
    ) {
        if (currentWord.length() > 0) {
            tokens.add(new Word(currentWord.toString()));
            currentWord.setLength(0);
        }
    }

    private static boolean isSentenceTerminator(char ch) {
        return ch == '.' || ch == '!' || ch == '?';
    }

    private static boolean isInnerPunctuation(char ch) {
        return ch == ',' || ch == ';' || ch == ':' || ch == '(' || ch == ')' || ch == '"' || ch == '\'';
    }
}

class Sentence {

    private final List<TextToken> tokens;

    public Sentence(List<TextToken> tokens) {
        this.tokens = new ArrayList<>(tokens);
    }


    // Нормалізує речення, видаляючи порожні слова.
    public void normalize() {
        tokens.removeIf(
                t -> (t instanceof Word) && ((Word) t).length() == 0
        );
    }


     // Видаляє слова заданої довжини,
     // що починаються з приголосної літери.
    public void removeWords(int length) {
        tokens.removeIf(
                t -> (t instanceof Word)
                        && ((Word) t).length() == length
                        && ((Word) t).startsWithConsonant()
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean lastWasWord = false;

        for (TextToken t : tokens) {
            if (t instanceof Word) {
                if (sb.length() > 0 && lastWasWord) {
                    sb.append(" ");
                }
                sb.append(t);
                lastWasWord = true;
            } else if (t instanceof Punctuation) {
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ' ') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append(t);
                lastWasWord = false;
            }
        }
        return sb.toString();
    }
}

interface TextToken {}

class Punctuation implements TextToken {

    private final char value;

    public Punctuation(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

class Word implements TextToken {

    private final Letter[] letters;

    public Word(String rawWord) {
        letters = new Letter[rawWord.length()];
        for (int i = 0; i < rawWord.length(); i++) {
            letters[i] = new Letter(rawWord.charAt(i));
        }
    }

    public int length() {
        return letters.length;
    }

    public char firstChar() {
        return letters[0].getValue();
    }


    // Перевіряє, чи починається слово з приголосної літери.
    public boolean startsWithConsonant() {
        String vowels = "АЕЄИІЇОУЮЯаеєиіїоуюяAEIOUYaeiouy";
        return vowels.indexOf(firstChar()) == -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Letter l : letters) {
            sb.append(l.getValue());
        }
        return sb.toString();
    }
}

class Letter {

    private final char value;

    public Letter(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }
}
