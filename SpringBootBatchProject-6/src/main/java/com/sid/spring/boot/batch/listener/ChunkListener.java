/**
 * 
 */
package com.sid.spring.boot.batch.listener;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * @author Lenovo
 *
 */
public class ChunkListener {

	@BeforeChunk
	public void beforeChunkListener(ChunkContext chunkContext) {
		System.out.println("-------> Before Chunk execution <----------");
	}

	@AfterChunk
	public void afterChunkListener(ChunkContext chunkContext) {
		System.out.println("-------> After Chunk execution <----------");
	}
}
