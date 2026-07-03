package com.marianagoto.catimagelist.domain.usecase

import com.marianagoto.catimagelist.data.dto.ImageDto
import com.marianagoto.catimagelist.data.repository.CatRepository
import kotlinx.coroutines.flow.Flow

class GetCatsUseCase(
    private val repository: CatRepository
) {
    /**
     * Executa o caso de uso de buscar gatos.
     * @param limit Quantidade de gatos (padrão: 20)
     * @return Result com lista de CatImage ou erro
     */
    operator fun invoke(limit: Int = 20): Flow<List<ImageDto>> {
        return repository.getCatsList(limit)
    }
}