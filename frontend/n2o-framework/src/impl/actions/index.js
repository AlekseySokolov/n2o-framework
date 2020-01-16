import dummyImpl from './dummy';
import exportTable from './exportTable/exportTable';
import perform from './perform/perform';
import link from './link/link';
import exportModal from '../../components/widgets/Table/ExportModal';
import ToggleColumn from '../../components/actions/Dropdowns/ToggleColumn';
import ChangeSize from '../../components/actions/Dropdowns/ChangeSize';

export default {
  dummyImpl: dummyImpl,
  exportTable: exportTable,
  perform,
  link,
  exportModal,
  ToggleColumn,
  ChangeSize,
};
