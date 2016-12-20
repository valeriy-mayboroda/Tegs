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
    public static ArrayList<String> tags = new ArrayList<>();//Итоговый список тегов

    public boolean mistake = false;//Ошибка, количество открытых тегов не равно количеству закрытых
    public String tagName;
    public String openTag;
    public String closeTag;
    public String tagLine;//Строка для хранения промежуточного результата
    public Tag (String tagName) {
        this.tagName = tagName;
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
    public void makeTagLine (String lineForFound) {
        int currentIndex = 0;//Текущая позиция в поисковом выражении для строки lineForFound
        Pattern patternOpenTag = Pattern.compile(openTag);
        Matcher matcherOpenTag;
        matcherOpenTag = patternOpenTag.matcher(lineForFound);
        while (matcherOpenTag.find() && !mistake) {
            tagLine = matcherOpenTag.group();
            currentIndex = matcherOpenTag.end();
            //Подрезаем строку, чтобы метод поиска по закрытому тегу повторно не захватил начало строки
            lineForFound = lineForFound.substring(currentIndex, lineForFound.length());
            currentIndex = makeMoreClosed(lineForFound);
            //Подрезаем строку, чтобы текущий метод поиска повторно не захватил то, что было добавлено методом makeMoreClosed()
            lineForFound = lineForFound.substring(currentIndex, lineForFound.length());
            if (!mistake) tags.add(tagLine);
            matcherOpenTag = patternOpenTag.matcher(lineForFound);
        }
    }
    public int makeMoreClosed(String matcherLine) {
        int currentIndex = 0;//Текущая позиция в поисковом выражении для строки matcherLine
        Pattern patternCloseTag = Pattern.compile(".*?" + closeTag);
        Matcher matcherCloseTag = patternCloseTag.matcher(matcherLine);
        int openTagSum = tagSum(tagLine, openTag);
        int closeTagSum = tagSum(tagLine, closeTag);
        while (openTagSum > closeTagSum && !mistake) {
            if (matcherCloseTag.find()) {
                tagLine += matcherCloseTag.group();
                currentIndex = matcherCloseTag.end();
                closeTagSum++;
                //Так как матч по закрытому тегу мог захватить новый открытый тег, то пересчитываем количество открытых тегов
                openTagSum = tagSum(tagLine, openTag);
            }
            else {
                mistake = true;
            }
        }
        return currentIndex;
    }
    public void printResult (String lineForFound) {
        int currentIndex = 0;//Текущая позиция в поисковом выражении для строки lineForFound
        Pattern patternOpenTag = Pattern.compile(openTag);
        Matcher matcherOpenTag;
        lineForFound = lineForFound.replaceFirst(openTag, "");//Убираем первый открытый тег
        matcherOpenTag = patternOpenTag.matcher(lineForFound);
        while (matcherOpenTag.find() && !mistake) {
            tagLine = matcherOpenTag.group();
            currentIndex = matcherOpenTag.end();
            lineForFound = lineForFound.substring(currentIndex, lineForFound.length());
            currentIndex = makeMoreClosed(lineForFound);
            lineForFound = lineForFound.substring(currentIndex, lineForFound.length());
            if (!mistake) System.out.println(tagLine);
            if (tagSum(tagLine, openTag) > 1) {
                printResult(tagLine);
            }
            matcherOpenTag = patternOpenTag.matcher(lineForFound);
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
            for (String s: tags){
                System.out.println(s);
                first.printResult(s);
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}

