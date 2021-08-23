package com.nixer.nprox.dao;

import com.nixer.nprox.entity.common.SysLoginType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (SysLoginType)表数据库访问层
 *
 * @author makejava
 * @since 2021-07-01 18:39:20
 */
@Repository
public interface SysLoginTypeDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SysLoginType queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<SysLoginType> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param sysLoginType 实例对象
     * @return 对象列表
     */
    List<SysLoginType> queryAll(SysLoginType sysLoginType);

    /**
     * 新增数据
     *
     * @param sysLoginType 实例对象
     * @return 影响行数
     */
    int insert(SysLoginType sysLoginType);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysLoginType> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<SysLoginType> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysLoginType> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<SysLoginType> entities);

    /**
     * 修改数据
     *
     * @param sysLoginType 实例对象
     * @return 影响行数
     */
    int update(SysLoginType sysLoginType);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

     SysLoginType findByLoginName(String username);

    SysLoginType findByUserId(long userid);

    SysLoginType findByUserIdAndLoginType(@Param("userid")long userid,@Param("type") int type);

    void updateBind(SysLoginType sysLoginType);
}

