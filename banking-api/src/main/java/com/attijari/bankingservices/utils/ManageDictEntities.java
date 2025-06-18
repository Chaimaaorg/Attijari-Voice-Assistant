package com.attijari.bankingservices.utils;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ManageDictEntities {
    Long userId;

    Map<String,Object> entitiesDic;


}
