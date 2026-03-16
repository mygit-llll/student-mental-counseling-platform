package com.zoctan.api.mapper;

import com.zoctan.api.dto.CounselorDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ConsultationMapper {
    @Select("SELECT a.id, a.name, a.public_key " +  // ← 新增 a.public_key
            "FROM account a " +
            "INNER JOIN account_role ar ON a.id = ar.account_id " +
            "WHERE ar.role_id = #{roleId}")
    List<CounselorDTO> selectCounselorsByRoleId(@Param("roleId") Long roleId);
}