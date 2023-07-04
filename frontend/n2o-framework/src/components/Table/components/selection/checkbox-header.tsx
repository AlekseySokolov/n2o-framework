import React, { useCallback, VFC } from 'react'

import { CheckboxHeaderCellProps } from '../../models/props'
import { excludeItems, getAllValuesByKey } from '../../utils'
// @ts-ignore - отсутствует типизация
import Checkbox from '../../../controls/Checkbox/CheckboxN2O'
import { useTableActions } from '../../provider/TableActions'
import { useTableRefProps } from '../../provider/TableRefProps'

export const CheckboxHeaderCell: VFC<CheckboxHeaderCellProps> = ({ areAllRowsSelected }) => {
    const refProps = useTableRefProps()
    const { selectRows, deselectRows } = useTableActions()
    const onSelect = useCallback((event) => {
        const allRowsId = getAllValuesByKey(refProps.current.data, { keyToIterate: 'children', keyToExtract: 'id' })

        if (event.nativeEvent.target.checked) {
            const newSelection = excludeItems(allRowsId, refProps.current.selectedRows)

            selectRows(newSelection)
        } else {
            deselectRows(allRowsId)
        }
    }, [deselectRows, selectRows, refProps])

    return (
        <div className="checkbox-cell">
            <Checkbox
                inline
                checked={areAllRowsSelected}
                onChange={onSelect}
            />
        </div>
    )
}
