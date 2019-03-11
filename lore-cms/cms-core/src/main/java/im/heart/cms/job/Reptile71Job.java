package im.heart.cms.job;

import im.heart.cms.entity.Article;
import im.heart.cms.service.ArticleService;
import im.heart.core.utils.DateUtilsEx;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class Reptile71Job {
    //http://www.71.cn/2019/0306/1036178.shtml
    @Autowired
    ArticleService articleService;
    //http://www.71.cn/acastudies/expcolumn/
    //http://www.71.cn/acastudies/expcolumn/politics/1.shtml
    //http://www.71.cn/acastudies/expcolumn/economy/1.shtml
    @Scheduled(cron = "0 35 21 * * ?")
    void executeJob()throws Exception{
        log.info(".....................");
        parseArticleList("http://www.71.cn/acastudies/expcolumn/economy","经济");
    }

    private Map<String ,String> expcolumn(){
        String url="http://www.71.cn/acastudies/expcolumn/";
        try{
            URL uri=new URL(url);
            Document htmlEle=Jsoup.parse(uri,3000);
            Element liEle=htmlEle.select("a.cur[href="+url+"]").parents().first();
            Elements aEles=liEle.select("ul.subvideotree").select("a");
            for(Element aEle: aEles){
                String type=aEle.attr("title");
                String href=aEle.attr("href");
                parseArticleList(href,type);
            }
        }catch (MalformedURLException e){
            log.error(url);
            log.error(e.getStackTrace()[0].getMethodName(), e);
        }catch (IOException e){
            log.error(url);
            log.error(e.getStackTrace()[0].getMethodName(), e);
        }
        return null;
    }
    @Async
    public void parseArticleList(String url,String type){
        try {
            Document listEle=Jsoup.parse(new URL(url),3000);
            Elements articleEle=listEle.select(".articlelist_title a");
            for (Element article:articleEle){
                String articleUrl=article.attr("href");
                try {
                    Thread.sleep( RandomUtils.nextLong(10,20));
                    parseArticle(articleUrl,type);
                } catch (InterruptedException e) {
                    log.error(url);
                    log.error(e.getStackTrace()[0].getMethodName(), e);
                }
            }

            Node page=listEle.select(".page_box li").last();

            String aUrl=page.childNode(0).attr("href");
            parseArticleList(aUrl,type);
        } catch (IOException e) {
            log.error(url);
        }
    }


    @Async
    public   Article parseArticle(String url,String type){
        Article entity=null;
        try
        {
            URL uri=new URL(url);
            entity= new Article();
            entity.setType(type);
            Document html=Jsoup.parse(uri,3000);
            Elements tIco=html.select("a.t-ico");
            String href=tIco.attr("href");
            String idStr=StringUtils.substringAfter(href,"contentid=");
            if (StringUtils.isBlank(idStr)){
                log.info(url);
                return null;
            }
            BigInteger id=new BigInteger(idStr);
            boolean exist=this.articleService.existsById(id);
            if (exist){
                log.info(url);
                return null;
            }
            entity.setId(id);
            Elements keywordsEle=html.select("meta[name=keywords]");
            String keywords=keywordsEle.attr("content");
            entity.setSeoKeywords(keywords);
            Elements descriptionEle=html.select("meta[name=description]");
            String description=descriptionEle.attr("content");
            entity.setSeoDescription(description);
            Elements article=html.select(".article-main");
            Elements titleEle=article.select(".article-title");
            String title= titleEle.text();
            entity.setTitle(title);
            Elements subtitle=article.select(".article-subtitle");
            Elements editorsEle=article.select(".article-infos .editors");
            String editors=editorsEle.text();
            entity.setAuthor(editors);
            Elements dateEle=article.select(".article-infos .date");
            Elements sourceEle=article.select(".article-infos .source");
            String source=sourceEle.text();
            Elements describe=article.select("#describe");
            Elements contentEle=article.select("#article-content");
            entity.setObtainUrl(url);
            url=StringUtils.substringAfter(url,"//");
            url=StringUtils.substringAfter(url,"/");
            entity.setIsPub(Boolean.TRUE);
            try {
                entity.setPushTime(DateUtilsEx.stringToDate(dateEle.text(),"yyyy-MM-dd HH:mm"));
            } catch (ParseException e) {
                log.error(url);
                log.error(e.getStackTrace()[0].getMethodName(), e);
            }
            entity.setUrl(url);
            entity.setSource(source);
            entity.setContent(contentEle.outerHtml());
            entity.setCategoryId(BigInteger.ZERO);
            entity.setUserId(BigInteger.ZERO);
            this.articleService.save(entity);
        }catch (MalformedURLException e){
            log.error(url);
            log.error(e.getStackTrace()[0].getMethodName(), e);
        }catch (IOException e){
            log.error(url);
            log.error(e.getStackTrace()[0].getMethodName(), e);
        }
        return entity;
    }
}
