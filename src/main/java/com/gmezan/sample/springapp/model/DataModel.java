package com.gmezan.sample.springapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataModel implements Serializable {
	private String id;
	private String name;
	private String description;
	private String value;
}
