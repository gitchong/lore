package im.heart.front.web;


import com.google.common.collect.Lists;
import im.heart.cms.entity.Article;
import im.heart.cms.service.ArticleService;
import im.heart.cms.vo.ArticleVO;
import im.heart.core.CommonConst;
import im.heart.core.plugins.persistence.DynamicPageRequest;
import im.heart.core.plugins.persistence.DynamicSpecifications;
import im.heart.core.plugins.persistence.SearchFilter;
import im.heart.core.web.AbstractController;
import im.heart.material.entity.Periodical;
import im.heart.material.service.PeriodicalService;
import im.heart.material.vo.PeriodicalVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

/**
 *
 */
@Controller
@Slf4j
public class IndexController extends AbstractController {
	@Autowired
	private ArticleService articleService;
	@Autowired
	private PeriodicalService periodicalService;
	@RequestMapping(value={"/index","/",""},method = RequestMethod.GET)
	public ModelAndView welcome(HttpServletRequest request, HttpServletResponse response,
								ModelMap model) {
		articlesTop10(request,model);
		docsTop10(request,model);
		super.success(model);
		return new ModelAndView("index");
	}
	public void articlesTop10(HttpServletRequest request,ModelMap model){
		final Collection<SearchFilter> filters= DynamicSpecifications.buildSearchFilters(request);
		filters.add(new SearchFilter("isPub", SearchFilter.Operator.EQ,Boolean.TRUE));
		Specification<Article> spec= DynamicSpecifications.bySearchFilter(filters, Article.class);
		PageRequest pageRequest= DynamicPageRequest.buildPageRequest(1,10, "pushTime",CommonConst.Page.ORDER_DESC,Article.class);
		Page<Article> pag = this.articleService.findAll(spec, pageRequest);
		if(pag!=null&&pag.hasContent()){
			List<ArticleVO> vos = Lists.newArrayList();
			for(Article po:pag.getContent()){
				vos.add(new ArticleVO(po));
			}
			Page<ArticleVO> docVos  =new PageImpl<ArticleVO>(vos,pageRequest,pag.getTotalElements());
			model.put("articles",docVos);
		}
	}

	public void docsTop10(HttpServletRequest request,ModelMap model){
		final Collection<SearchFilter> filters= DynamicSpecifications.buildSearchFilters(request);
		filters.add(new SearchFilter("isPub", SearchFilter.Operator.EQ,Boolean.TRUE));
		Specification<Periodical> spec= DynamicSpecifications.bySearchFilter(filters, Periodical.class);
		PageRequest pageRequest= DynamicPageRequest.buildPageRequest(1,5, "pushTime",CommonConst.Page.ORDER_DESC,Periodical.class);
		Page<Periodical> pag = this.periodicalService.findAll(spec, pageRequest);
		if(pag!=null&&pag.hasContent()){
			List<PeriodicalVO> vos = Lists.newArrayList();
			for(Periodical po:pag.getContent()){
				vos.add(new PeriodicalVO(po));
			}
			Page<PeriodicalVO> docVos  =new PageImpl<PeriodicalVO>(vos,pageRequest,pag.getTotalElements());
			model.put("docs",docVos);
		}
	}

}
