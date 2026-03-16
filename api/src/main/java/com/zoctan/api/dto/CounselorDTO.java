package com.zoctan.api.dto;

import lombok.Data;

import javax.persistence.Id;

@Data
public class CounselorDTO {
    @Id
    private Long id;
    private String name;
    private String publicKey;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
}