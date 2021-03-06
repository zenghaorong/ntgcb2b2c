package com.aebiz.app.wx.modules.models;

import com.aebiz.baseframework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by gaoen on 2017-2-15 15:35:18
 */
@Table("wx_tpl_list")
@TableIndexes({@Index(name = "INDEX_WX_TPL_LIST", fields = {"template_id","wxid"}, unique = true)})
public class Wx_tpl_list extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("ig(view.tableName,'')")})
    private String id;

    @Column
    @Comment("模板ID")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String template_id;

    @Column
    @Comment("模板标题")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String title;

    @Column
    @Comment("主营行业")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String primary_industry;

    @Column
    @Comment("副营行业")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String deputy_industry;

    @Column
    @Comment("模板内容")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String content;

    @Column
    @Comment("模板示例")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String example;

    @Column
    @Comment("微信ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String wxid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrimary_industry() {
        return primary_industry;
    }

    public void setPrimary_industry(String primary_industry) {
        this.primary_industry = primary_industry;
    }

    public String getDeputy_industry() {
        return deputy_industry;
    }

    public void setDeputy_industry(String deputy_industry) {
        this.deputy_industry = deputy_industry;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getWxid() {
        return wxid;
    }

    public void setWxid(String wxid) {
        this.wxid = wxid;
    }
}
