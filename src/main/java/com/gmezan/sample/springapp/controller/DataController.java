package com.gmezan.sample.springapp.controller;

import com.gmezan.sample.springapp.model.DataModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class DataController {

	@GetMapping
	public Mono<ResponseEntity<DataModel>> getData() {
		return Mono.just(ResponseEntity.ok(DataModel.builder()
				.id("1")
						.description("this is the description")
						.name("data model")
						.value("value of the data")
				.build()));
	}
}
