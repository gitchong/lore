package im.heart.front.web;

import im.heart.cms.entity.Article;
import im.heart.cms.service.ArticleService;
import im.heart.core.CommonConst;
import im.heart.core.plugins.persistence.DynamicPageRequest;
import im.heart.core.plugins.persistence.DynamicSpecifications;
import im.heart.core.web.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

@Controller
public class ArticleController extends AbstractController {
    protected static final String apiVer = "/article";
    protected static final String VIEW_LIST="front/article/article_list";
    protected static final String VIEW_DETAILS="front/article/article_details";
    protected static final String VIEW_CREATE="front/article/article_create";
    @Autowired
    private ArticleService articleService;

    @GetMapping(value = apiVer+"/{id}")
    protected ModelAndView findById(
            @RequestParam(value = "jsoncallback", required = false) String jsoncallback,
            @PathVariable BigInteger id,
            @RequestParam(value = "access_token", required = false) String token,
            HttpServletRequest request,
            ModelMap model) {
        this.updateHitsById(id);
        Article po = this.articleService.findById(id);
        super.success(model, po);
        return new ModelAndView(VIEW_DETAILS);
    }
    @GetMapping(apiVer+"s")
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(value = "jsoncallback", required = false) String jsoncallback,
                             @RequestParam(value = "page", required = false, defaultValue = CommonConst.Page.DEFAULT_PAGE+"") Integer page,
                             @RequestParam(value = "size", required = false, defaultValue = CommonConst.Page.DEFAULT_SIZE+"") Integer size,
                             @RequestParam(value = "sort", required = false) String sort,
                             @RequestParam(value = "order", required = false,defaultValue = CommonConst.Page.DEFAULT_ORDER) String order,
                             @RequestParam(value = "access_token", required = false) String token,
                             ModelMap model) {
        Specification<Article> spec=DynamicSpecifications.bySearchFilter(request, Article.class);
        PageRequest pageRequest=DynamicPageRequest.buildPageRequest(page,size,sort,order,Article.class);
        Page<Article> pag = this.articleService.findAll(spec, pageRequest);
        super.success(model,pag);
        return new ModelAndView(VIEW_LIST);
    }

    public void updateHitsById(BigInteger id){
        this.articleService.addUpdateHitsTask(id);
    }
}
