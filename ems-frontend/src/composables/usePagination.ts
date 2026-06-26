import { ref, reactive, type ComputedRef, type Ref } from 'vue'

export interface PaginationParams {
  page: number
  size: number
  [key: string]: any
}

export interface PageResult<T> {
  records: T[]
  total: number
  [key: string]: any
}

export interface UsePaginationOptions<T> {
  defaultPageSize?: number
  defaultPage?: number
  defaultParams?: Record<string, any>
  fetchFn: (params: PaginationParams) => Promise<PageResult<T>>
  onSuccess?: (result: PageResult<T>) => void
}

export function usePagination<T>(options: UsePaginationOptions<T>) {
  const {
    defaultPageSize = 10,
    defaultPage = 1,
    defaultParams = {},
    fetchFn,
    onSuccess
  } = options

  const loading = ref(false)
  const tableData = ref<T[]>([]) as Ref<T[]>
  const total = ref(0)

  const params = reactive<PaginationParams>({
    page: defaultPage,
    size: defaultPageSize,
    ...defaultParams
  })

  const fetchData = async () => {
    loading.value = true
    try {
      const result = await fetchFn({ ...params })
      tableData.value = result.records
      total.value = result.total
      onSuccess?.(result)
    } finally {
      loading.value = false
    }
  }

  const handleSearch = () => {
    params.page = 1
    fetchData()
  }

  const handleReset = () => {
    Object.assign(params, {
      page: defaultPage,
      size: defaultPageSize,
      ...defaultParams
    })
    fetchData()
  }

  const handleSizeChange = (size: number) => {
    params.size = size
    params.page = 1
    fetchData()
  }

  const handleCurrentChange = (page: number) => {
    params.page = page
    fetchData()
  }

  const refresh = () => {
    fetchData()
  }

  return {
    loading,
    tableData,
    total,
    params,
    fetchData,
    handleSearch,
    handleReset,
    handleSizeChange,
    handleCurrentChange,
    refresh
  }
}
