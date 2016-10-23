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
    public static void main(String[] args) {
        String tag = args[0];

        String regex_some_Begin_one_End_tag = "<" + tag + "((>)|(\\s+?.*?>)).*?</" + tag + ">";//Матчим выражение с первого открытого
        //тега до первого закрытого тега (при этом открытых в конечном выражении может быть несколько, а закрытый захватываем первый)

        String regexBegin = ".*?<" + tag + "((>)|(\\s+?.*?>)).*?";

        String regexEnd = ".*?</" + tag + ">";//Матчим от одного закрытого тега до следующего закрытого тега, захватывая текст между ними

        ArrayList<String> list = new ArrayList();//Список тегов, по нему формируем вывод результата

        Pattern pattern_some_Begin_one_End_tag = Pattern.compile(regex_some_Begin_one_End_tag);
        Pattern pattern_Text_one_End_tag = Pattern.compile(regexEnd);

        try (BufferedReader reader     = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader filereader = new BufferedReader(new FileReader(reader.readLine()))) {
            String line = "";
            String result = "";
            while ((line = filereader.readLine()) != null) {
                result += line;
            }
            Matcher matcherall = pattern_some_Begin_one_End_tag.matcher(result);
            Matcher matcherend = pattern_Text_one_End_tag.matcher(result);

            while (matcherall.find()) {
                int sum_tagBegin = 1;//Количество открытых тегов в текущем поиске
                int sum_tagEnd = 1;//Количество закрытых тегов в текущем поиске
                boolean crush = false;//Ошибка, количество открытых тегов не совпадает с количеством закрытых

                String sout = matcherall.group();//Этой строкой формируем наш список

                matcherend.find();//Здесь подгоняем поиск по закрытому тегу к общему поиску, чтобы оба поиска стартовали с одного места.
                matcherend.group();

                //Удаляя открытые теги подсчитываем их количество
                String tagBegin = sout.replaceFirst(regexBegin, "");
                while (tagBegin.matches(regexBegin)) {
                    sum_tagBegin ++;
                    tagBegin = tagBegin.replaceFirst(regexBegin, "");
                }

                while (sum_tagBegin > sum_tagEnd)
                {
                    if (matcherend.find()) {
                        String tagEnd = matcherend.group();
                        sout += tagEnd;
                        sum_tagEnd ++;
                        //Если поиск по закрытому тегу захватил новый открытый тег (пересчитываем количество, подгоняем поиски)
                        while (tagEnd.matches(regexBegin)) {
                            matcherall.find();//Здесь подгоняем общий поиск к поиску по закрытому тегу, чтобы оба поиска стартовали с одного места.
                            matcherall.group();
                            sum_tagBegin ++;
                            tagEnd = tagEnd.replaceFirst(regexBegin, "");
                        }
                    }
                    else {
                        crush = true;
                        break;
                    }
                }
                if (crush) break;
                else list.add(sout);
            }
            for (String vivod : list) // Вывод хромает, если вложенность > 3
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