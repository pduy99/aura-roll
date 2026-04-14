package com.helios.auraroll.indexing

import kotlinx.coroutines.flow.Flow

interface PhotoIndexer {
    fun index(): Flow<IndexingState>
}