package im.heart.search.entity;

import com.alibaba.fastjson.annotation.JSONField;
import im.heart.core.CommonConst.FlowStatus;
import im.heart.core.entity.AbstractEntity;
import im.heart.core.enums.Status;
import im.heart.core.utils.FileUtilsEx;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author gg
 * 材料上传记录表
 */
@DynamicUpdate()
@DynamicInsert()
@Data
@Document(indexName = "periodical",type = "periodical", shards = 1,replicas = 0, refreshInterval = "-1")
public class Periodical implements AbstractEntity<Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 4316965064793331760L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(length = 32, name = "ID", nullable = false, unique = true, updatable = false)
	private Long id;

	@Column(length = 32, name = "USER_ID", nullable = false, unique = true, updatable = false)
	private Long userId;

	@Column(name = "AUTHOR", nullable = false,length=128 , updatable = false)
	private String author;


	@Column(name = "ORIGIN_PRICE", nullable = false,length=64 )
	private BigDecimal originPrice;
	@Column(name = "FINAL_PRICE", nullable = false,length=64 )
	private BigDecimal finalPrice;

	@Column(name = "CATEGORY_ID", nullable = false,length=64 )
	private Long categoryId;

	@Column(name = "CATEGORY_CODE", nullable = false,length=128 )
	private String categoryCode;

	@Column(name = "PERIODICAL_CODE", nullable = false,length=64 , updatable = false)
	private String periodicalCode;

	@Column(name = "PERIODICAL_NAME", nullable = false,length=64)
	private String periodicalName;

	/**
	 * 文件大小
	 */
	@Column(name = "DATA_SIZE", nullable = false)
	private Long dataSize=0L;
	@Transient
	private String dataSizeHuman;
	@Transient
	private String zipSizeHuman;

	/**
	 * 压缩数据包大小
	 */
	@Column(name = "ZIP_SIZE", nullable = false)
	private Long zipSize=0L;
	/**
	 * 总页码
	 */
	@Column(name = "PAGE_NUM", nullable = false)
	private Integer pageNum=0;

	@Column(name = "PERIODICAL_TYPE", nullable = false,length=32)
	private String periodicalType;

	@Column(name = "CITY_ID", nullable = false,length=64)
	private String cityId;

	@Formula(value = "(select model.area_name from dic_frame_area model where model.area_code = city_id)")
	private String cityName;

	@Column(name = "PATH_URL", nullable = false,length=256)
	private String pathUrl;

	@JSONField(serialize = false)
	@Column(name = "REAL_FILE_PATH", nullable = false,length=512)
	private String realFilePath;

	/** 页面标题. */
	@Column(name = "SEO_TITLE", nullable = false)
	@Length(max = 200)
	private String seoTitle="";

	/** 页面关键词. */
	@Length(max = 200)
	@Column(name = "SEO_KEYWORDS", nullable = false)
	private String seoKeywords="";

	/** 页面描述 . */
	@Length(max = 200)
	@Column(name = "SEO_DESC", nullable = false)
	private String seoDesc="";

	@Column(name = "CONTENT", nullable = false)
	private String content;
	/**
	 * 封面图片
	 */
	@Column(name = "COVER_IMG_URL", nullable = false,length=256)
	private String  coverImgUrl;
	@Column(name = "RATE", nullable = false)
	private  Integer rate=0;

	/** 点击数 . */
	@Column(name = "HITS", nullable = false, length=20)
	private Long hits=0L;
	/**
	 * //下载次数
	 */
	@Column(name = "DOWN_TIMES", nullable = false, length=20)
	private Long downTimes=0L;

	/** 是否发布. */
	@NotNull
	@Column(name = "IS_PUB", nullable = false)
	private Boolean isPub=Boolean.FALSE;

	/** 是否置顶. */
	@NotNull
	@Column(name = "IS_TOP",nullable = false)
	private Boolean isTop=Boolean.FALSE;

	@Column(name = "FILE_HEADER", nullable = false,length=32)
	private String fileHeader;

	@Column(name = "CHECK_STATUS", nullable = false)
	@Enumerated(EnumType.STRING)
	private Status checkStatus= Status.pending;

	@Column(name = "STATUS", nullable = false)
	@Enumerated(EnumType.STRING)
	private FlowStatus status= FlowStatus.initial;
	/**
	 *  发布日期
	 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "PUSH_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date pushTime;
	@JSONField (format="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(nullable=false, name = "CREATE_TIME" ,updatable = false)
	private Date createTime;

	@NotNull
	@JSONField (format="yyyy-MM-dd HH:mm:ss" )
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(nullable=false, name = "MODIFY_TIME" )
	private Date modifyTime;
	@PrePersist
	protected void onCreate() {
		createTime = new Date();
		modifyTime = new Date();
		pushTime = new Date();
	}
	@PreUpdate
	protected void onUpdate() {
		modifyTime = new Date();
		if(isPub){
			pushTime = new Date();
		}
	}
	public String getDataSizeHuman() {
		return FileUtilsEx.getHumanReadableFileSize(dataSize);
	}
	public String getZipSizeHuman() {
		return FileUtilsEx.getHumanReadableFileSize(zipSize);
	}
}
