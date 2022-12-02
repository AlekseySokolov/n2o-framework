import React from 'react'
import uniqueId from 'lodash/uniqueId'
import has from 'lodash/has'
import get from 'lodash/get'
import isNil from 'lodash/isNil'
import { SHOW_ALL, SHOW_CHILD, SHOW_PARENT } from 'rc-tree-select'

import { Icon } from '../../snippets/Icon/Icon'
import { Badge } from '../../snippets/Badge/Badge'
import { isBadgeLeftPosition, isBadgeRightPosition, resolveBadgeProps } from '../../snippets/Badge/utils'
import { Shape } from '../../snippets/Badge/enums'

export const visiblePartPopup = (
    item,
    {
        prefixCls,
        iconFieldId,
        imageFieldId,
        labelFieldId,
        badge,
    },
) => {
    const {
        fieldId: badgeFieldId,
        position: badgePosition,
    } = badge || {}

    const hasBadge = has(item, badgeFieldId)

    return (
        <span className={`${prefixCls}-content-wrapper`}>
            {[
                has(item, iconFieldId) && (
                    <Icon key={uniqueId('tree_icon_')} name={item[iconFieldId]} />
                ),
                !isNil(item[imageFieldId]) && (
                    <div className={`${prefixCls}-image-tree-wrapper`}>
                        <img
                            alt="not found"
                            key={uniqueId('tree_img_')}
                            src={item[imageFieldId]}
                        />
                    </div>
                ),
                hasBadge && isBadgeLeftPosition(badgePosition) &&
                    <Badge {...resolveBadgeProps(badge, item)} shape={badge?.shape || Shape.Square} />,
                has(item, labelFieldId) && (
                    <span key={uniqueId('tree_label_')} className={`${prefixCls}-label`}>
                        {item[labelFieldId]}
                    </span>
                ),
                hasBadge && isBadgeRightPosition(badgePosition) &&
                    <Badge {...resolveBadgeProps(badge, item)} shape={badge?.shape || Shape.Square} />,
            ]}
        </span>
    )
}

const STRATEGIES = {
    parent: SHOW_PARENT,
    child: SHOW_CHILD,
    all: SHOW_ALL,
}

export const getCheckedStrategy = (key) => {
    const strategy = get(STRATEGIES, key)

    if (strategy) {
        return strategy
    }

    return SHOW_ALL
}
