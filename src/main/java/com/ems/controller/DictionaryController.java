package com.ems.controller;

import com.ems.common.RequireRole;
import com.ems.common.Result;
import com.ems.config.CacheConfig;
import com.ems.entity.Dictionary;
import com.ems.service.DictionaryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/{type}")
    public Result<List<Dictionary>> listByType(@PathVariable String type,
                                               @RequestParam(required = false) String parentCode) {
        return Result.success(dictionaryService.listByType(type, parentCode));
    }

    @GetMapping
    public Result<List<Dictionary>> listAll() {
        return Result.success(dictionaryService.list());
    }

    @PostMapping
    @RequireRole("admin")
    @CacheEvict(value = CacheConfig.CACHE_DICTIONARY, allEntries = true)
    public Result<Void> save(@RequestBody Dictionary dictionary) {
        boolean success = dictionaryService.save(dictionary);
        return success ? Result.success() : Result.error("保存失败");
    }

    @PutMapping("/{id}")
    @RequireRole("admin")
    @CacheEvict(value = CacheConfig.CACHE_DICTIONARY, allEntries = true)
    public Result<Void> update(@PathVariable Long id, @RequestBody Dictionary dictionary) {
        dictionary.setId(id);
        boolean success = dictionaryService.updateById(dictionary);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = dictionaryService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
