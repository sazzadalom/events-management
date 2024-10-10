package com.alom.configuration.properties;


import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Validated
@Component
@ConfigurationProperties(prefix = "excel.header.props")
public class ExcelProperties {

	private List<String> attendeeUploadHeaders;
}
