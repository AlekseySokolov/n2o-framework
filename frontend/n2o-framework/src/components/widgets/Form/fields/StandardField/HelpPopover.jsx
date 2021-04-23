import React, { createRef } from 'react'
import PropTypes from 'prop-types'
import UncontrolledPopover from 'reactstrap/lib/UncontrolledPopover'
import PopoverBody from 'reactstrap/lib/PopoverBody'

import { id } from '../../../../../utils/id'

class HelpPopover extends React.Component {
    constructor(props) {
        super(props)

        this.fieldId = id()
        this.button = createRef()
    }

  focusOnClick = () => {
      this.button.current.focus()
  };

  render() {
      const { help, placement, icon } = this.props
      return (
          <div className="n2o-popover">
              <button
                  onClick={this.focusOnClick}
                  className="n2o-popover-btn"
                  id={this.fieldId}
                  ref={this.button}
              >
                  <i className={icon} />
              </button>
              <UncontrolledPopover
                  className="n2o-popover-body"
                  placement={placement}
                  target={this.fieldId}
                  trigger="focus"
              >
                  <PopoverBody>
                      <div dangerouslySetInnerHTML={{ __html: help }} />
                  </PopoverBody>
              </UncontrolledPopover>
          </div>
      )
  }
}

HelpPopover.propTypes = {
    help: PropTypes.oneOfType([PropTypes.string, PropTypes.node]),
}

HelpPopover.defaultProps = {
    placement: 'right',
    icon: 'fa fa-question-circle',
}

export default HelpPopover
