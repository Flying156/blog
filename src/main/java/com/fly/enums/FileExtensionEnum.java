package com.fly.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文件扩展名枚举
 *
 * @author Milk
 */
@Getter
@AllArgsConstructor
public enum FileExtensionEnum {

    /**
     * JPEG 文件
     */
    JPG("jpg", "JPEG 文件"),

    /**
     * JPEG 文件
     */
    JPEG("jpeg", "JPEG 文件"),

    /**
     * PNG 文件
     */
    PNG("png", "PNG 文件"),

    /**
     * WAV 文件
     */
    WAV("wav", "WAV 文件"),

    /**
     * Markdown 文件
     */
    MD("md", "Markdown 文件"),

    /**
     * 文本文件
     */
    TXT("txt", "文本文件");

    /**
     * 扩展名
     */
    private final String extensionName;

    /**
     * 描述
     */
    private final String description;

    private static final Map<String, FileExtensionEnum> FILE_EXTENSION_ENUM_MAP;

    /*
      将枚举转换成Map
     */
    static{
        FILE_EXTENSION_ENUM_MAP = Arrays.stream(FileExtensionEnum.values())
                .collect(Collectors.toMap(FileExtensionEnum::getExtensionName, Function.identity()
                ,(firstEnum, secondEnum) -> firstEnum, // 如果键值冲突，选择第一个
                         () -> CollectionUtils.newHashMap(FileExtensionEnum.values().length)));

    }

    public static FileExtensionEnum get(String extensionName){
        FileExtensionEnum fileExtensionEnum = FILE_EXTENSION_ENUM_MAP.get(extensionName);
        if(fileExtensionEnum == null){
            throw new RuntimeException("文件扩展名不存在");
        }
        return fileExtensionEnum;
    }

}
