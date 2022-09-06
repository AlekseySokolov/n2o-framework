import { createContext } from 'react'

/* eslint-disable no-unused-vars,  @typescript-eslint/no-unused-vars */
export const METHODS = {
    fetchData() {},
    setFilter(filterModel) {},
    setResolve(model) {},
    setEdit(model) {},
    setSelected(models) {},
    setSorting(sorting) {},
    setPage(page) {},
    setSize(size) {},
}

export const DataSourceContext = createContext(METHODS)
