import React from 'react'
import get from 'lodash/get'
import { ButtonDropdownProps } from 'reactstrap'

// @ts-ignore ignore import error from js file
import { WithDataSource } from '../../../core/datasource/WithDataSource'
import { getFromSource, metaPropsType, IItem } from '../../utils'
import { IDataSourceModels } from '../../../core/datasource/const'
import { ITEM_TYPE } from '../../constants'
import { Action } from '../../Action/Action'

import { Link } from './NavItems/Links/Link'
import { Dropdown } from './NavItems/Dropdown/Dropdown'

interface INavItemContainer {
    itemProps: IItem
    active: boolean
    datasources: metaPropsType[]
    models: IDataSourceModels
    direction: ButtonDropdownProps['direction']
}

const NavItemContainer = (props: INavItemContainer) => {
    const { itemProps, active, datasources, models, direction } = props
    const datasource = get(itemProps, 'datasource')

    const item = getFromSource(itemProps, datasources, datasource, models)
    const { type } = item

    if (type === ITEM_TYPE.ACTION) {
        return <Action item={item} from="HEADER" className="nav-item" />
    }

    if (type === ITEM_TYPE.DROPDOWN) {
        return <Dropdown className="nav-item" active={active} direction={direction} {...item} />
    }

    if (type === ITEM_TYPE.LINK) {
        return <Link item={item} active={active} />
    }

    return null
}

export default WithDataSource(NavItemContainer)

export { NavItemContainer }
