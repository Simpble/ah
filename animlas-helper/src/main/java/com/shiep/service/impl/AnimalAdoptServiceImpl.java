package com.shiep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shiep.mapper.ILocationDao;
import com.shiep.dao.IUserDao;
import com.shiep.mapper.*;
import com.shiep.entity.*;
import com.shiep.service.IAnimalAdoptService;
import com.shiep.vo.AnimalAdoptCommentVo;
import com.shiep.vo.AnimalAdoptVo;
import com.shiep.vo.Category;
import com.shiep.vo.Location;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalAdoptServiceImpl implements IAnimalAdoptService {
    @Resource
    private IAnimalAdoptMapper animalAdoptMapper;

    @Resource
    private IProvinceMapper provinceMapper;

    @Resource
    private ICityMapper cityMapper;

    @Resource
    private ICountyMapper countyMapper;

    @Resource
    private IUserMapper userMapper;

    @Resource
    private ILocationDao locationDao;

    @Autowired
    private IUserDao userDao;

    @Resource
    private IAnimalCategoryDetailsMapper animalCategoryDetailsMapper;

    @Resource
    private IPhotoAnimalAdoptMapper photoAnimalAdopt;

    @Resource
    private IAnimalStatusMapper animalStatusMapper;

    @Resource
    private IPhotoAnimalAdoptMapper photoAnimalAdoptMapper;

    @Resource
    private IAnimalAdoptCommentMapper animalAdoptCommentMapper;


    @Transactional
    @Override
    /*保存动物收养记录,此处开启了事务过程*/
    public boolean saveAnimalAdopt(AnimalAdopt animalAdopt) {
        if (animalAdopt == null) {
            return false;
        }
        return this.animalAdoptMapper.insert(animalAdopt) > 0;
    }

    @Override
    /*county为分区，city为城市,category为宠物的种类，
    * 目前来看当前方法存在较大问题，后续需要修改！！！！！！！
    * 当前方法的作用目前来看是作为条件查询的方法*/
    public List<AnimalAdoptVo> queryAll(Long cityId, Integer categoryId, Long countyId) {
        List<AnimalAdopt> animalAdopts = null;

        // 1、）什么都不做  首先必须处理的是cityId 如果为null  查询所有adopt
        if (cityId == null) {
            QueryWrapper<AnimalAdopt> qw = new QueryWrapper<>();
            qw.lambda().eq(AnimalAdopt::getDeleteStatus, 0);
            animalAdopts = this.animalAdoptMapper.selectList(qw);
            List<AnimalAdoptVo> animalAdoptVo = this.getAnimalAdoptVo(animalAdopts);
            return animalAdoptVo;
        }

        // 2、）只能点击宠类过滤，即只做宠类过滤 县区为null
        /*在后续的过程中cityId应不为空*/
//        if (cityId == null && categoryId != null && categoryId != 0) {
        if (categoryId != null && categoryId != 0) {
            // 根据categoryId 查询出categoryDetails符合的
            QueryWrapper<AnimalCategoryDetails> qwDetails = new QueryWrapper<>();
            qwDetails.lambda().eq(AnimalCategoryDetails::getCategoryId, categoryId);
            List<AnimalCategoryDetails> categoryDetails = this.animalCategoryDetailsMapper.selectList(qwDetails);
            List<Integer> categoryIds = categoryDetails.stream().map(AnimalCategoryDetails::getId).collect(Collectors.toList());
            if (categoryIds.isEmpty()) {
                return new ArrayList<>();
            }
            QueryWrapper<AnimalAdopt> qw = new QueryWrapper<>();
            qw.lambda().in(AnimalAdopt::getCategoryId, categoryIds);
            animalAdopts = this.animalAdoptMapper.selectList(qw);

            List<AnimalAdoptVo> animalAdoptVo = this.getAnimalAdoptVo(animalAdopts);
            return animalAdoptVo;

        }

        // 3、）、选了地区后没有店家具体的县区也没有点击宠类
        /*上述过程中只对*/
//        if (cityId != null && (categoryId == null || categoryId == 0) && countyId == null) {
        if ((categoryId == null || categoryId == 0) && countyId == null) {
            // 首先根据cityId查询出所有的县区id
            QueryWrapper<County> qwCounty = new QueryWrapper<>();
            qwCounty.lambda().eq(County::getCityId, cityId);
            List<County> counties = this.countyMapper.selectList(qwCounty);
            List<Long> countyIds = counties.stream().map(County::getId).collect(Collectors.toList());
            // 查找
            QueryWrapper<AnimalAdopt> qw = new QueryWrapper<>();
            qw.lambda().eq(AnimalAdopt::getDeleteStatus, 0).in(AnimalAdopt::getCountyId, countyIds);
            animalAdopts = this.animalAdoptMapper.selectList(qw);

            List<AnimalAdoptVo> animalAdoptVo = this.getAnimalAdoptVo(animalAdopts);
            return animalAdoptVo;
        }


        // 4、）选择地区后，点击了具体的县区，但是没有点击宠类  cityId不为null  即根据县区来查 不管宠类
        if (cityId != null && (categoryId == null || categoryId == 0) && countyId != null) {
            QueryWrapper<AnimalAdopt> qw = new QueryWrapper<>();
                // 查询adopt中sa_count_id
            qw.lambda().eq(AnimalAdopt::getCountyId, countyId);
            animalAdopts = this.animalAdoptMapper.selectList(qw);

            List<AnimalAdoptVo> animalAdoptVo = this.getAnimalAdoptVo(animalAdopts);
            return animalAdoptVo;
        }

        // 接下来是过滤查询

        // 5、）点击了宠类之前已经点击了具体县区  如果宠类过滤器发生了变化,且已经点击了地区 此时的县区过滤也要加上即所有县区
        if (cityId != null && categoryId != null && categoryId != 0 && countyId != null) {
            List<AnimalAdopt> adopts = this.animalAdoptMapper.queryAdoptByCategoryIdAndCountyId(countyId, categoryId);
            List<AnimalAdoptVo> animalAdoptVo = this.getAnimalAdoptVo(adopts);
            return animalAdoptVo;
        }

        // 6)、选择了地区后没有点击具体的县区，但是点击了宠类  此时只能按照cityId查出来具体县区 然后和宠类一起过滤
        if (cityId != null && categoryId != null && categoryId != 0 && countyId == null) {
            List<AnimalAdopt> adopts = this.animalAdoptMapper.queryAdoptByCityIdAndCategoryId(cityId, categoryId);
            List<AnimalAdoptVo> animalAdoptVo = this.getAnimalAdoptVo(adopts);
            return animalAdoptVo;
        }

        return null;
    }


    @Override
    /*方法作用：根据用户id查询当前用户收养的宠物*/
    public List<AnimalAdopt> queryAdoptByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        List<AnimalAdopt> adopts = this.animalAdoptMapper.queryAdoptByUserId(userId);
        if (CollectionUtils.isEmpty(adopts)) {
            return null;
        }
        return adopts;
    }

    @Override
    /*方法作用：根据收养表id删除当前记录*/
    public boolean removeAnimalAdoptById(Long id) {
        if (id == null) {
            return false;
        }
        if (this.animalAdoptMapper.removeAnimalAdoptById(id) > 0) {
            return true;
        }
        return false;
    }

    /*
    * by theAning
    * 方法作用：将AnimalAdopt转换为AnimalAdoptVo对象
    * */
    public AnimalAdoptVo animalAdoptConvertToAnimalAdoptVo(AnimalAdopt animalAdopt){
        AnimalAdoptVo vo = new AnimalAdoptVo();

        User user = this.userDao.getUserById(animalAdopt.getUserId());
        Location location = this.locationDao.getLocationByCountyId(animalAdopt.getCountyId());
        Category category = this.animalCategoryDetailsMapper.getCategoryById(animalAdopt.getCategoryId());
        List<PhotoAnimalAdopt> photos = this.photoAnimalAdopt.getPhotoByAnimalAdoptId(animalAdopt.getId());
        AnimalStatus animalStatus = this.animalStatusMapper.selectById(animalAdopt.getStatusId());

        vo.setAnimalAdopt(animalAdopt);
        vo.setUser(user);
        vo.setLocation(location);
        vo.setCategory(category);
        vo.setAnimalStatus(animalStatus);
        vo.setPhotoAnimalAdopts(photos);

        return vo;
    }

    @Override
    /*方法作用：目前来看是获取用户信息以及宠物信息封装至一个对象当中*/
    public AnimalAdoptVo getAnimalAdoptDetailsById(Long id) {
        if (id == null) {
            return null;
        }

        QueryWrapper<AnimalAdopt> qw = new QueryWrapper<>();
        qw.lambda().eq(AnimalAdopt::getId, id);
        AnimalAdopt a = this.animalAdoptMapper.selectOne(qw);

        if (a == null) {
            return null;
        }
        return animalAdoptConvertToAnimalAdoptVo(a);
    }

    /*与上述方法作用一致，此方法为将集合中的对象集体转化*/
    public List<AnimalAdoptVo>  getAnimalAdoptVo(List<AnimalAdopt> animalAdopts) {
        List<AnimalAdoptVo> adoptVos = new ArrayList<>();
        if (CollectionUtils.isEmpty(animalAdopts)) {
            return null;
        }
        for (AnimalAdopt a: animalAdopts) {
            adoptVos.add(animalAdoptConvertToAnimalAdoptVo(a));
        }
        return adoptVos;
    }

    @Override
    /*方法作用：将数据库中的所有AnimalAdopt对象转换为AnimalAdoptVo对象返回*/
    public List<AnimalAdoptVo> queryAllAdoptInPageBack() {
        List<AnimalAdopt> adopts = this.animalAdoptMapper.selectList(null);
        List<AnimalAdoptVo> animalAdoptVos = this.getAnimalAdoptVo(adopts);
        if (CollectionUtils.isEmpty(animalAdoptVos)) {
            return null;
        }
        return animalAdoptVos;
    }

    @Override
    /*方法作用：查询所有动物的状态。动物的状态包含：流浪中，被救助，是否领养，是否寻找到*/
    public List<AnimalStatus> queryAllAnimalStatus() {
        List<AnimalStatus> statusList = this.animalStatusMapper.selectList(null);
        if (CollectionUtils.isEmpty(statusList)) {
            return null;
        }
        return statusList;
    }

    @Override
    /*方法作用：根据id查询动物收养*/
    public AnimalAdopt queryAnimalAdoptById(Long id) {
        if (id == null) {
            return null;
        }
//        AnimalAdopt animalAdopt = this.animalAdoptMapper.selectById(id);
        return this.animalAdoptMapper.selectById(id);
    }

    @Override
    /*方法作用：查询当前宠物的所有照片*/
    public List<PhotoAnimalAdopt> queryPhotoByAdoptId(Long id) {
        if (id == null) {
            return null;
        }
        QueryWrapper<PhotoAnimalAdopt> qw = new QueryWrapper<>();
        qw.lambda().eq(PhotoAnimalAdopt::getAnimalAdoptId, id);
        List<PhotoAnimalAdopt> list = this.photoAnimalAdopt.selectList(qw);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list;
    }

    @Override
    /*方法作用：修改信息，对应当前类的‘改’ */
    public boolean updateAdoptBack(AnimalAdopt editAdoptInfo) {
        if (editAdoptInfo.getId() == null) {
            return false;
        }
        UpdateWrapper<AnimalAdopt> qw = new UpdateWrapper<>();
        qw.eq("id", editAdoptInfo.getId());
        return this.animalAdoptMapper.update(editAdoptInfo,qw) > 0;
    }

    @Override
    /*批量删除*/
    public boolean deleteMultipleAdoptByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        for (Long id : ids) {
            try {
                if (id != null) {
                    this.animalAdoptMapper.removeAnimalAdoptById(id);
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @Override
    /*方法作用：模糊查询*/
    public List<AnimalAdoptVo> queryAdoptByLike(String key, String value) {
        //StringUtils.isBlank的作用为检查字符串是否为空字符串，空字符串包含空字符，以及null
        if (StringUtils.isBlank(value)) {
            List<AnimalAdopt> adopts = this.animalAdoptMapper.selectList(null);
            return this.getAnimalAdoptVo(adopts);
        }
        QueryWrapper<AnimalAdopt> qw = new QueryWrapper<>();
        qw.like(key, value);
        List<AnimalAdopt> adopts = this.animalAdoptMapper.selectList(qw);
        List<AnimalAdoptVo> animalAdoptVo = this.getAnimalAdoptVo(adopts);

        if (CollectionUtils.isEmpty(animalAdoptVo)) {
            return null;
        }
        return animalAdoptVo;
    }

    @Override
    /*方法作用：保存评论*/
    public boolean saveAdoptComment(AnimalAdoptComment adoptComment) {
        if (adoptComment == null) {
            return false;
        }
        return this.animalAdoptCommentMapper.insert(adoptComment) > 0;
    }

    /*
     * by theAning
     * 方法作用：将AnimalAdoptComment转换为AnimalAdoptCommentVo对象
     * */
    public AnimalAdoptCommentVo animalAdoptCommentConvertToAnimalAdoptCommentVo(AnimalAdoptComment comment){
        AnimalAdoptCommentVo vo = new AnimalAdoptCommentVo();
        vo.setAdoptComment(comment);

        QueryWrapper<User> qwUser = new QueryWrapper<>();
        qwUser.lambda().eq(User::getId, comment.getCommentUserId());
        User user = this.userMapper.selectOne(qwUser);

        vo.setUser(user);
        return vo;
    }

    @Override
    /*方法作用：查询属于当前adopt的评论内容*/
    public List<AnimalAdoptCommentVo> queryCommentByAdoptId(Long adoptId) {
        if (adoptId == null) {
            return null;
        }
        List<AnimalAdoptCommentVo> commentVos = new ArrayList<>();

        QueryWrapper<AnimalAdoptComment> qw = new QueryWrapper<>();
        qw.lambda().eq(AnimalAdoptComment::getDeleteStatus, 0).eq(AnimalAdoptComment::getAdoptId, adoptId);
        List<AnimalAdoptComment> list = this.animalAdoptCommentMapper.selectList(qw);

        for(AnimalAdoptComment comment : list) {
            commentVos.add(animalAdoptCommentConvertToAnimalAdoptCommentVo(comment));
        }

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return commentVos;
    }

    @Override
    /*将数据库中的所有adoptComment转换为AdoptCommentVo进行存储*/
    public List<AnimalAdoptCommentVo> queryAllCommentInPageBack() {
        List<AnimalAdoptCommentVo> commentVos = new ArrayList<>();
        List<AnimalAdoptComment> list = this.animalAdoptCommentMapper.selectList(null);
        for(AnimalAdoptComment comment : list) {
            commentVos.add(animalAdoptCommentConvertToAnimalAdoptCommentVo(comment));
        }
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return commentVos;
    }

    @Override
    /*删除评论*/
    public boolean deleteCommentById(Long id) {
        if (id == null) {
            return false;
        }
        return this.animalAdoptCommentMapper.deleteCommentById(id) > 0;
    }

    @Override
    /*方法作用：批量删除评论，为逻辑删除*/
    public boolean deleteMultipleCommentsByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        for (Long id : ids) {
            if (id != null)
            try {
                this.animalAdoptCommentMapper.deleteCommentById(id);
            } catch (Exception e) {
                System.out.println(e) ;
                return false;
            }
        }
        return true;
    }

    @Override
    /*方法作用：将评论恢复*/
    public boolean rollbackMultipleCommentsByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        for (Long id : ids) {
            if (id != null)
                try {
                    //将评论恢复，删除也为逻辑删除.
                    this.animalAdoptCommentMapper.rollbackCommentById(id);
                } catch (Exception e) {
                    System.out.println(e) ;
                    return false;
                }
        }
        return true;
    }

    /*方法作用：将集合中的评论批量转换commentVo对象*/
    public List<AnimalAdoptCommentVo> getAnimalAdoptCommentVo(List<AnimalAdoptComment> comments) {
        if (CollectionUtils.isEmpty(comments)) {
            return null;
        }
        List<AnimalAdoptCommentVo> commentVos = new ArrayList<>();
        for(AnimalAdoptComment comment : comments) {
            commentVos.add(animalAdoptCommentConvertToAnimalAdoptCommentVo(comment));
        }
        return commentVos;
    }


    @Override
    /*方法作用：模糊查询评论*/
    public List<AnimalAdoptCommentVo> queryAllCommentLikeInPageBack(String key, String value) {
        if (StringUtils.isBlank(value)) {
            List<AnimalAdoptComment> adopts = this.animalAdoptCommentMapper.selectList(null);
            return this.getAnimalAdoptCommentVo(adopts);
        }


        QueryWrapper<AnimalAdoptComment> qw = new QueryWrapper<>();
        qw.like(key, value);
        List<AnimalAdoptComment> adopts = this.animalAdoptCommentMapper.selectList(qw);
        List<AnimalAdoptCommentVo> animalAdoptVo = this.getAnimalAdoptCommentVo(adopts);

        if (CollectionUtils.isEmpty(animalAdoptVo)) {
            return null;
        }
        return animalAdoptVo;
    }

    @Override
    /*将删除的adopt批量恢复*/
    public boolean rollbackMultipleAdoptByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        for (Long id : ids) {
            if (id != null)
                try {
                    this.animalAdoptMapper.rollbackMultipleAdoptById(id);
                } catch (Exception e) {
                    System.out.println(e) ;
                    return false;
                }
        }
        return true;
    }
}
