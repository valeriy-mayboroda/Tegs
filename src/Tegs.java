/*Created by val on 04.10.2016.*/
/* Ситайте с консоли имя файла, который имеет HTML-формат. Пример:
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

public class Tegs
{
    public static void main(String[] args)
    {
        String teg = args[0];
        String regex = "<\\s*?"+teg+"((>)|(\\s+?.*?\\s*?>)).*?<\\s*?/\\s*?"+teg+"((>)|(\\s*?>))";
        String regexbegin = ".*?<\\s*?"+teg+"((>)|(\\s+?.*?\\s*?>)).*?";
        String regexend = ".*?<\\s*?/\\s*?"+teg+"((>)|(\\s*?>))";
        ArrayList<String> list = new ArrayList();
        Pattern pattern = Pattern.compile(regex);
        Pattern patternend = Pattern.compile(regexend);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader filereader = new BufferedReader(new FileReader(reader.readLine()) ))
        {
            String line;
            String result = "";
            while ((line = filereader.readLine()) != null)
            {
                result += line;
            }
            Matcher matcherall = pattern.matcher(result);
            Matcher matcherend = patternend.matcher(result);
            while (matcherall.find())
            {
                int begin = 1;
                int end = 1;
                boolean crush = false;
                String A = matcherall.group();
                matcherend.find();
                matcherend.group();//Подгоняем поиск по закрытому тегу к общему поиску
                String B = A.replaceFirst(regexbegin, "");
                while (B.matches(regexbegin))
                {
                    begin ++;
                    B = B.replaceFirst(regexbegin, "");
                }
                while (begin > end)
                {
                    if (matcherend.find())
                    {
                        String C = matcherend.group();
                        A += C;
                        end ++;
                        while (C.matches(regexbegin))
                        {
                            matcherall.find();
                            matcherall.group();//Подгоняем общий поиск к поиску по закрытому тегу
                            begin ++;
                            C = C.replaceFirst(regexbegin, "");
                        }
                    }
                    else
                    {
                        crush = true;
                        break;
                    }
                }
                if (crush) break;
                else list.add(A);
            }
            for (String s : list) // Вывод хромает, если вложенность > 3
            {
                System.out.println(s);
                s = s.replaceFirst(regexbegin, "");
                matcherall = pattern.matcher(s);
                String s1 = "";
                while (matcherall.find())
                {
                    int indexend = matcherall.end();
                    s1 += matcherall.group();
                    s = s.substring(indexend, s.length());
                    matcherend = patternend.matcher(s);
                    while(matcherend.find())
                    {
                        indexend = matcherend.end();
                        if (s.substring(indexend, s.length()).matches(regexend))
                            s1 += matcherend.group();
                        else break;
                    }
                    System.out.println(s1);
                    s = s1.replaceFirst(regexbegin, "");
                    s1 = "";
                    matcherall = pattern.matcher(s);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getStackTrace());
        }
    }
}
