package im.heart.material.parser.impl;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.HanLP;
import im.heart.core.CommonConst;
import im.heart.core.utils.DateUtilsEx;
import im.heart.core.utils.FileUtilsEx;
import im.heart.core.utils.StringUtilsEx;
import im.heart.material.entity.Periodical;
import im.heart.material.entity.PeriodicalImg;
import im.heart.material.parser.PeriodicalParser;
import im.heart.material.parser.PeriodicalParser;
import im.heart.material.service.PeriodicalImgService;
import im.heart.material.service.PeriodicalService;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jodconverter.DocumentConverter;
import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.jodconverter.document.DocumentFormat;
import org.jodconverter.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
public class PeriodicalParserImpl implements PeriodicalParser {
    protected static final Logger logger = LoggerFactory.getLogger(PeriodicalParserImpl.class);
    protected static final String  FILE_ROOT_PATH= CommonConst.STATIC_UPLOAD_ROOT;
    @Resource
    private DocumentConverter documentConverter;
    @Autowired
    private PeriodicalService materialPeriodicalService;
    @Value("${prod.upload.path.root}")
    private String uploadFilePath="";
    @Autowired
    private PeriodicalImgService materialPeriodicalImgService;
    @Override
    public void parser(Periodical periodical, InputStream is) {
        String suffixes=periodical.getFileHeader();
        String realFilePath=periodical.getRealFilePath();
        File targetFile=new File(realFilePath+".pdf");
        DocumentFormat documentFormat= DefaultDocumentFormatRegistry.getInstance().getFormatByExtension(suffixes);
        try {
            this.documentConverter.convert(is,true).as(documentFormat).to(targetFile).as(DefaultDocumentFormatRegistry.PDF).execute();
            Integer pageNum=this.pdf2Image(targetFile, "",10,periodical);
        } catch (OfficeException e) {
            e.printStackTrace();
        } finally {
        }
    }

    @Async
    @Override
    public void addParserTask(Periodical periodical, InputStream is) {
        parser(periodical,is);
    }

    /***
     * PDF文件转PNG图片
     * @param pdfFile pdf文件
     * @param dstImgFolder 图片存放的文件夹
     * @param maxPage 页数 为0则转换全部页数
     * @return
     */
    @Async
    public Integer pdf2Image(File pdfFile, String dstImgFolder, int maxPage, Periodical periodical) {
        PDDocument pdDocument=null;
        //pdf 总页数
        Integer pageNum=0;
        //@param dpi dpi越大转换后越清晰，相对转换速度越慢
        int dpi=100;
        try {
            if(StringUtilsEx.isBlank(dstImgFolder)){
                dstImgFolder = pdfFile.getParent() ;
            }
            pdDocument = PDDocument.load(pdfFile);
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            int pages = pdDocument.getNumberOfPages();
            pageNum=pages;
            if(maxPage > 0&&maxPage<pages) {
                pages = maxPage;
            }
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            String content = pdfTextStripper.getText(pdDocument);
            if(StringUtilsEx.isBlank(periodical.getSeoKeywords())){
                List<String> seoKeywords=HanLP.extractKeyword(content, 13);
                //设置关键词
                periodical.setSeoKeywords(StringUtilsEx.join(seoKeywords,","));
            }
            if(StringUtilsEx.isBlank(periodical.getSeoKeywords())){
                List<String> summary= HanLP.extractSummary(content,5);;
                //生成文章摘要
                periodical.setSeoDesc(StringUtilsEx.join(summary,","));
            }
            periodical.setContent(content);
            List<PeriodicalImg> entities= Lists.newArrayList();
            BigInteger periodicalId= periodical.getId();
            String periodicalCode=periodical.getPeriodicalCode();
            String periodicalType=periodical.getPeriodicalType();
            String cityId=periodical.getCityId();
            String imgFilePathPrefix = dstImgFolder+File.separator+periodicalCode+File.separator+periodicalId+File.separator;
            for (int i = 0; i < pages; i++) {
                int page=i+1;
                String fileNameKey=periodicalId+"_"+page+".png";
                File dstFile = new File(imgFilePathPrefix+fileNameKey);
                if (!dstFile.exists()) {
                    dstFile.mkdirs();
                }
                BufferedImage image = renderer.renderImageWithDPI(i, dpi);
                ImageIO.write(image, "png", dstFile);
                PeriodicalImg materialPeriodicalImg=new PeriodicalImg();
                materialPeriodicalImg.setCityId(cityId);
                materialPeriodicalImg.setPageNum(page);
                materialPeriodicalImg.setPeriodicalCode(periodicalCode);
                materialPeriodicalImg.setPeriodicalType(periodicalType);
                String url  = StringUtilsEx.replace(dstFile.getPath(), File.separator, "/");
                url =StringUtilsEx.replace(url, StringUtilsEx.replace(uploadFilePath, File.separator, "/") , "");
                String imgUrl="/"+FILE_ROOT_PATH+"/"+url;
                materialPeriodicalImg.setImgUrl(imgUrl);
                materialPeriodicalImg.setPathUrl(dstFile.getPath());
                materialPeriodicalImg.setPeriodicalId(periodicalId);
                if(i==0){
                    periodical.setCoverImgUrl(imgUrl);
                }
                entities.add(materialPeriodicalImg);
            }
            periodical.setPageNum(pageNum);
            periodical.setImportLog(periodical.getImportLog()+ DateUtilsEx.timeToString(new Date()) +" 图片生成成功！<br/>");
            periodical.setStatus(CommonConst.FlowStatus.PROCESSED);
            this.materialPeriodicalService.save(periodical);
            this.materialPeriodicalImgService.saveAll(entities);
        } catch (Exception e) {
            logger.error(e.getStackTrace()[0].getMethodName(), e);
        }finally {
            IOUtils.closeQuietly(pdDocument);
            //删除pdf 文件
            logger.info("文件处理完毕，删除 pdf原文件");
            FileUtilsEx.deleteQuietly(pdfFile);
        }
        return pageNum;
    }

}
