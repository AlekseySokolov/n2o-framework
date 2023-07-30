import React from 'react'
import PropTypes from 'prop-types'
import has from 'lodash/has'

// fns
import { Badge } from '../../../../snippets/Badge/Badge'
import { isBadgeRightPosition, resolveBadgeProps } from '../../../../snippets/Badge/utils'
import { splitSearchText } from '../../until'
import { Shape } from '../../../../snippets/Badge/enums'

function BaseNode({
    prefixCls,
    imageFieldId,
    labelFieldId,
    badge,
    valueFieldId,
    searchValue,
    searchKeys,
    data,
    filter,
}) {
    const {
        fieldId: badgeFieldId,
        position: badgePosition,
    } = badge || {}

    const labelStyle = {
        order: isBadgeRightPosition(badgePosition) ? 0 : 1,
    }

    return (
        <span
            data-id={data[valueFieldId]}
            className={`${prefixCls}-content-wrapper cls-${data[valueFieldId]}`}
            tabIndex="-1"
        >
            {[
                has(data, imageFieldId) && (
                    <div className={`${prefixCls}-image-tree-wrapper`}>
                        <img
                            alt={data[labelFieldId]}
                            key={`tree_img_${data[valueFieldId]}`}
                            src={data[imageFieldId]}
                        />
                    </div>
                ),
                has(data, labelFieldId) && (
                    <span
                        key={`tree_label_${data[valueFieldId]}`}
                        className={`${prefixCls}-label`}
                        style={labelStyle}
                    >
                        {searchKeys.includes(data[valueFieldId]) && searchValue
                            ? splitSearchText(data[labelFieldId], searchValue, filter)
                            : data[labelFieldId]}
                    </span>
                ),
                (badge && has(data, badgeFieldId)) && (
                    <Badge
                        {...badge}
                        {...resolveBadgeProps(badge, data)}
                        shape={badge?.shape || Shape.Square}
                    />
                ),
            ]}
        </span>
    )
}

BaseNode.propTypes = {
    prefixCls: PropTypes.string,
    imageFieldId: PropTypes.string,
    labelFieldId: PropTypes.string,
    badge: PropTypes.object,
    valueFieldId: PropTypes.string,
    searchValue: PropTypes.string,
    searchKeys: PropTypes.string,
    data: PropTypes.object,
    filter: PropTypes.any,
}

export default BaseNode
