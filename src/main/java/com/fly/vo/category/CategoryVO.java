package com.fly.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryVO {
    @Schema(description = "分类 ID")
    private Integer id;


    @NotBlank(message = "分类名不能为空")
    @Schema(description = "分类名")
    private String categoryName;
}
