/* Ситайте с консоли имя файла, который имеет HTML-формат.
Пример:
Info about Leela <span xml:lang="en" lang="en"><b><span>Turanga Leela
</span></b></span><span>Super</span><span>girl</span>
Первым параметром в метод main приходит тег. Например, "span"
Вывести на консоль все теги, которые соответствуют заданному тегу
Каждый тег на новой строке, порядок должен соответствовать порядку следования в файле
Количество пробелов, \n, \r не влияют на результат
Файл не содержит тег CDATA, для всех открывающих тегов имеется отдельный закрывающий тег, одиночных тегов нет.
Тег может содержать вложенные теги
Пример вывода:
<span xml:lang="en" lang="en"><b><span>Turanga Leela</span></b></span>
<span>Turanga Leela</span> (выводим вложенный тег строки № 1)
<span>Super</span>
<span>girl</span>

Шаблон тега:
<tag>text1</tag>
<tag text2>text1</tag>
<tag
text2>text1</tag>

text1, text2 могут быть пустыми
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tag {
    private static ArrayList<String> tags = new ArrayList<>();//Итоговый список тегов
    private boolean mistake = false;//Ошибка, количество открытых тегов не равно количеству закрытых
    private String openTag;
    private String closeTag;
    private String tagLine;//Строка для хранения промежуточного результата
    public Tag (String tagName) {
        openTag = makeOpenTag(tagName);
        closeTag = makeCloseTag(tagName);
    }
    public String makeOpenTag (String tagName) {//Форимируем открытый тег
        String openTag = "<" + tagName + "((>)|(\\s+?.*?>))";
        return openTag;
    }
    public String makeCloseTag (String tagName) {//Форимируем закрытый тег
        String closeTag = "</" + tagName + ">";
        return closeTag;
    }
    public int tagSum (String line, String regex) {//Подсчитываем сумму тегов в строке line (regex - открытые или закрытые)
        int sum = 0;
        while (line.matches(".*?" + regex + ".*?")) {
            sum ++;
            line = line.replaceFirst(regex, "");
        }
        return sum;
    }
    public void makeTagLine (String baseLine) {
        int index = 0;//Текущая позиция в поисковом выражении для строки baseLine
        Pattern patternOpenTag = Pattern.compile(openTag);
        Matcher matcherOpenTag = patternOpenTag.matcher(baseLine);
        while (matcherOpenTag.find(index) && !mistake) {
            tagLine = matcherOpenTag.group();
            index = makeMoreClosed(baseLine, matcherOpenTag.end());
            if (!mistake) tags.add(tagLine);
            tagLine = tagLine.replaceFirst(openTag, "");
            if (tagSum(tagLine, openTag) > 0)
                makeTagLine(tagLine);
        }
    }
    public int makeMoreClosed(String baseLine, int index) {
        Pattern patternCloseTag = Pattern.compile(".*?" + closeTag);
        Matcher matcherCloseTag = patternCloseTag.matcher(baseLine);
        int openTagSum = tagSum(tagLine, openTag);
        int closeTagSum = tagSum(tagLine, closeTag);
        while (openTagSum > closeTagSum && !mistake) {
            if (matcherCloseTag.find(index)) {
                tagLine += matcherCloseTag.group();
                index = matcherCloseTag.end();
                closeTagSum++;
                //Так как матч по закрытому тегу мог захватить новый открытый тег, то пересчитываем количество открытых тегов
                openTagSum = tagSum(tagLine, openTag);
            }
            else mistake = true;
        }
        return index;
    }
    public void printResult () {
        for (String s: tags){
            System.out.println(s);
        }
    }
    public static void main(String[] args) {
        String tag = args[0];
        String fileContent = "";//Сюда читаем содержимое файла
        try (BufferedReader reader     = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader filereader = new BufferedReader(new FileReader(reader.readLine()))) {
            String line = "";
            while ((line = filereader.readLine()) != null) {
                fileContent += line;
            }
            Tag first = new Tag(tag);
            first.makeTagLine(fileContent);
            first.printResult();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}

