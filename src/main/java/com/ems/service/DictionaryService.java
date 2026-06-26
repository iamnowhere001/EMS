package com.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.Dictionary;

import java.util.List;

public interface DictionaryService extends IService<Dictionary> {

    List<Dictionary> listByType(String type);

    List<Dictionary> listByType(String type, String parentCode);
}
