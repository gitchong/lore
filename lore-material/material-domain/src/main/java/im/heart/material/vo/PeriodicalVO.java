package im.heart.material.vo;

import com.alibaba.fastjson.annotation.JSONField;
import im.heart.material.entity.Periodical;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
@Data
public class PeriodicalVO  extends Periodical {

    /**
     * //用户是否收藏该商品
     */
    private Boolean isCollect=Boolean.FALSE;

    /**
     * //用户是否购买过该商品
     */
    private Boolean isBuy=Boolean.FALSE;

    @JSONField(format="yyyy-MM-dd")
    private Date createTime;

    @JSONField (serialize = false)
    private Date modifyTime;
    @JSONField (serialize = false)
    private Boolean isPub;
//    @JSONField (serialize = false)
//    private Status checkStatus;

    @JSONField (serialize = false)
    private String pathUrl;

    @JSONField (serialize = false)
    private String realFilePath;

//    @JSONField (serialize = false)
//    private Status status;
    public PeriodicalVO(Periodical po){
        this(po,true);
    }
    public PeriodicalVO(Periodical po,boolean lazy){
        BeanUtils.copyProperties(po, this);
    }
}
