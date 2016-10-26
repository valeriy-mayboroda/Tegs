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

    public static String line;//Эти строки будем заносить в список tags

    public static String fileResult;//Сюда читаем содержимое файла

    public static boolean mistake = false;//Ошибка, количество открытых тегов не равно количеству закрытых

    public static String openTag (String tagName) { //Форимируем открытый тег
        String openTag = "<" + tagName + "((>)|(\\s+?.*?>))";
        return openTag;
    }

    public static String closeTag (String tagName) { //Форимируем закрытый тег
        String closeTag = "</" + tagName + ">";
        return closeTag;
    }

    public static int tagSum (String line, String regex) { //Подсчитываем сумму тегов (regex - открытые или закрытые)
        int tagSum = 0;
        while (line.matches(".*?" + regex + ".*?")) { //Вот зачем .* перед и после
            tagSum ++;
            line = line.replaceFirst(regex, "");
        }
        return tagSum;
    }

    public static void outLine (String tagName) {
        Pattern patternOpenTag = Pattern.compile(openTag(tagName));
        Matcher matcherOpenTag;
        matcherOpenTag = patternOpenTag.matcher(fileResult);
        while (matcherOpenTag.find()) {
            line = matcherOpenTag.group();
            fileResult = fileResult.substring(matcherOpenTag.end(), fileResult.length());
            moreClosed(tagName);
            if (mistake) break;
            else tags.add(line);
            matcherOpenTag = patternOpenTag.matcher(fileResult);
        }
    }

    public static void moreClosed(String tagName) {
        Pattern patternCloseTag = Pattern.compile(".*?" + closeTag(tagName));
        Matcher matcherCloseTag;
        int openTag = tagSum(line, openTag(tagName));
        int closeTag = tagSum(line, closeTag(tagName));
        while (openTag > closeTag) {
            matcherCloseTag = patternCloseTag.matcher(fileResult);
            if (matcherCloseTag.find()) {
                line += matcherCloseTag.group();
                fileResult = fileResult.substring(matcherCloseTag.end(), fileResult.length());
                closeTag++;
                //Так как матч по закрытому тегу мог захватить новый открытый тег, то пересчитываем количество открытых тегов
                openTag = tagSum(line, openTag(tagName));
            }
            else {
                mistake = true;
                break;
            }
        }
    }

    public static void main(String[] args) {
        String tag = args[0];

        String regexSomeBeginOneEndTag = "<" + tag + "((>)|(\\s+?.*?>)).*?</" + tag + ">";

        String regexBegin = ".*?<" + tag + "((>)|(\\s+?.*?>)).*?";

        String regexEnd = ".*?</" + tag + ">";

        Pattern pattern_some_Begin_one_End_tag = Pattern.compile(regexSomeBeginOneEndTag);
        Pattern pattern_Text_one_End_tag = Pattern.compile(regexEnd);

        try (BufferedReader reader     = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader filereader = new BufferedReader(new FileReader(reader.readLine()))) {
            String line = "";
            while ((line = filereader.readLine()) != null) {
                fileResult += line;
            }

            Matcher matcherall = pattern_some_Begin_one_End_tag.matcher(fileResult);
            Matcher matcherend = pattern_Text_one_End_tag.matcher(fileResult);
            outLine(tag);

            for (String vivod : tags) // Вывод хромает, если вложенность > 3 .вынести в функцию
            {
                System.out.println(vivod);
                vivod = vivod.replaceFirst(regexBegin, "");//Будем убирать по одному открытому тегу и выводить вложенные
                matcherall = pattern_some_Begin_one_End_tag.matcher(vivod);//Ищем вложенные теги
                String tagin = "";//Здесь промежуточно храним вложенный тег
                while (matcherall.find()) {
                    int indexend = matcherall.end();//Индекс, с которого начнется вложенный тег
                    tagin += matcherall.group();
                    vivod = vivod.substring(indexend, vivod.length());//Подрезали строку, чтобы начиналась с нашего вложенного тега
                    matcherend = pattern_Text_one_End_tag.matcher(vivod);
                    while(matcherend.find()) {
                        indexend = matcherend.end();
                        if (vivod.substring(indexend, vivod.length()).matches(regexEnd))
                            tagin += matcherend.group();
                        else break;
                    }

                    System.out.println(tagin);
                    vivod = tagin.replaceFirst(regexBegin, "");
                    tagin = "";
                    matcherall = pattern_some_Begin_one_End_tag.matcher(vivod);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}