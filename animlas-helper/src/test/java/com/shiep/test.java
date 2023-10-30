package com.shiep;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiep.entity.City;
import com.shiep.entity.Province;
import com.shiep.entity.Report;
import com.shiep.mapper.ICityMapper;
import com.shiep.mapper.IProvinceMapper;
import com.shiep.mapper.IReportMapper;
import com.shiep.service.IMailService;
import com.shiep.util.Md5Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
public class test {

    @Autowired
    public IMailService iMailService;

    @Resource
    public IProvinceMapper provinceMapper;

    @Resource
    public ICityMapper cityMapper;

    @Resource
    public IReportMapper reportMapper;

    @Test
    public void testMd5(){
        /*加密工具的使用：将相同的明码可解释为相同的密码,不需要逆转的过程*/
        String salt = Md5Utils.generateSalt();
        System.out.println(salt);
        String md5Password = Md5Utils.shaHex("123456", salt);
        System.out.println(md5Password);
        System.out.println(Md5Utils.shaHex("123456", salt));

    }
    @Test
    /*测试邮件发送*/
    public void test(){
//        iMailService.sendMailAndGenerateCode("309275570@qq.com");
        /*暂且认为是查询为当前日期，一个星期内，一个月内的报道*/
//        List<Report> reports = reportMapper.queryReportInDay();
//        List<Report> reports1 = reportMapper.queryReportInWeek();
//        List<Report> reports2 = reportMapper.queryReportInMonth();

        List<Report> reports = reportMapper.queryReportInDay(1L, null);
        System.out.println(reports);
    }


    @Test
    /*插入省份和市级名称以及区域名称*/
    public void insertProvinceAndCity(){
//         完成省份的插入
        String provinces = "北京市，天津市，上海市，重庆市，河北省，山西省，辽宁省，吉林省，黑龙江省，江苏省，浙江省，安徽省，福建省，江西省，山东省，河南省，湖北省，湖南省，广东省，海南省，四川省，贵州省，云南省，陕西省，甘肃省，青海省，台湾省，内蒙古自治区，广西壮族自治区，西藏自治区，宁夏回族自治区，新疆维吾尔自治区，香港特别行政区，澳门特别行政区";
        String[] provinceNums = provinces.split("，");
        for (String provinceName : provinceNums) {
            provinceMapper.insert(new Province(provinceName));
        }

        HashMap<String,String> map = new HashMap<>();
        map.put("河南省","郑州市、洛阳市、焦作市、商丘市、信阳市、周口市、鹤壁市、安阳市、濮阳市、驻马店市、" +
                "南阳市、开封市、漯河市、许昌市、新乡市、济源市、灵宝市、偃师市、邓州市、登封市、三门峡市、" +
                "新郑市、禹州市、巩义市、永城市、长葛市、义马市、林州市、项城市、汝州市、荥阳市、" +
                "平顶山市、卫辉市、辉县市、舞钢市、新密市、孟州市、沁阳市、郏县");
        map.put("安徽省","合肥市、亳州市、芜湖市、马鞍山市、池州市、黄山市、滁州市、安庆市、" +
                "淮南市、淮北市、蚌埠市、宿州市、宣城市、六安市、阜阳市、" +
                "铜陵市、明光市、天长市、宁国市、界首市、桐城市、潜山市");
        map.put("福建省","福州市、厦门市、泉州市、漳州市、南平市、三明市、龙岩市、莆田市宁德市、" +
                "龙海市、建瓯市、武夷山市、长乐市、福清市、晋江市、南安市、福安市、邵武市、石狮市、福鼎市、建阳市、漳平市、永安市、");
        map.put("湖南省","长沙市、郴州市、益阳市、娄底市、株洲市、衡阳市、湘潭市、" +
                "岳阳市、常德市、邵阳市、永州市、张家界市、怀化市、浏阳市、" +
                "醴陵市、湘乡市、耒阳市、沅江市、涟源市、常宁市、吉首市、" +
                "津市市、冷水江市、临湘市、汨罗市、武冈市、韶山市、湘西州");
        map.put("甘肃省","兰州市、白银市、武威市、金昌市、平凉市、张掖市、嘉峪关市、酒泉市、" +
                "庆阳市、定西市、陇南市、天水市、玉门市、临夏市、合作市、敦煌市、甘南州");
        map.put("河北省","石家庄市、唐山市、秦皇岛市、邯郸市、邢台市、保定市、张家口市、承德市、沧州市、廊坊市、衡水市"
                );
        map.put("山西省","太原市、大同市、阳泉市、长治市、晋城市、朔州市、晋中市、运城市、忻州市、临汾市、吕梁市");
        map.put("内蒙古自治区","呼和浩特市、包头市、乌海市、赤峰市、通辽市、鄂尔多斯市、呼伦贝尔市、巴彦淖尔市、乌兰察布市");
        map.put("辽宁省","沈阳市、大连市、鞍山市、抚顺市、本溪市、丹东市、锦州市、营口市、阜新市、辽阳市、盘锦市、铁岭市、朝阳市、葫芦岛市");
        Set<String> provinceNames = map.keySet();
        for (String provinceName : provinceNames) {
            convert(provinceName,map.get(provinceName));
        }

    }


    public void convert(String provinceName,String cityString){
        String[] citys = cityString.split("、");
        LambdaQueryWrapper<Province> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Province::getName,provinceName);
        Province province = provinceMapper.selectOne(lqw);
        Long provinceId = province.getId();
        for (String city : citys) {
            cityMapper.insert(new City(city,provinceId));
        }
    }
}
