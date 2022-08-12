/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.demo.core;

import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import javax.annotation.Nonnull;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/13 1:51 下午
 */
public class TestImportSelector implements ImportSelector {

    @Nonnull
    @Override
    public String[] selectImports(@Nonnull AnnotationMetadata annotationMetadata) {
        // 不使用默认的TypeFilter
        //        ClassPathScanningCandidateComponentProvider provider =
        //                new ClassPathScanningCandidateComponentProvider(false);
        //        // 添加扫描规律规则，这里指定了内置的注解过滤规则
        //        provider.addIncludeFilter(new AnnotationTypeFilter(StateService.class));
        //        // 获取扫描结果的全限定类名
        //        List<String> className = new ArrayList<>();
        //        // 扫描指定包，如果有多个包，这个过程可以执行多次
        String[] packages = getPackageToScan(annotationMetadata);
        //        for (String aPackage : packages) {
        //            Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents(aPackage);
        //            beanDefinitionSet.forEach(beanDefinition -> className.add(beanDefinition.getBeanClassName()));
        //        }
        //        String[] classNameArray = new String[className.size()];
        return new String[0];
    }

    private String[] getPackageToScan(AnnotationMetadata metadata) {
        //通过自定义注解获取自定义包路径
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(ComponentScan.class.getName()));
        String[] basePackages = attributes.getStringArray("basePackages");
        if (basePackages.length != 0) {
            return basePackages;
        }
        return new String[]{ClassUtils.getPackageName(metadata.getClassName())};
    }

}

