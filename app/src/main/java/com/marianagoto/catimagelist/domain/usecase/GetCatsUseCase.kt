package com.marianagoto.catimagelist.domain.usecase

import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.domain.model.CatImage

class GetCatsUseCase(
    private val repository: CatRepository
) {
    /**
     * Executa o caso de uso de buscar gatos.
     * @param limit Quantidade de gatos (padrão: 20)
     * @return Result com lista de CatImage ou erro
     */
    suspend operator fun invoke(limit: Int = 20): Result<List<CatImage>> {
        return repository.getCatsList(limit)
    }
}