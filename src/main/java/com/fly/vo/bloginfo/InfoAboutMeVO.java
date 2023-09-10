package com.fly.vo.bloginfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 关于我信息
 * @author Milk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoAboutMeVO {

    /**
     * 关于我信息
     */
    @NotBlank
    @Schema(description = "关于我内容")
    private String aboutContent;

}
