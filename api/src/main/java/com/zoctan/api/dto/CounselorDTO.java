package com.zoctan.api.dto;

import lombok.Data;

import javax.persistence.Id;

@Data
public class CounselorDTO {
    @Id
    private Long id;
    private String name;
}