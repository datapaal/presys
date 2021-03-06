import React from 'react';
import Row from 'components/elements/Row';
import Column from 'components/elements/Column';
import InfoTable from 'components/elements/InfoTable';

const Barn = ({
fnr,
navn,
fellesFor0591,
fellesEtter0591 }) => <Row>
  <Column size={6}>
    <InfoTable>
      <tr>
        <td>Fødselsnummer</td>
        <td>{fnr}</td>
      </tr>
      <tr>
        <td>Navn</td>
        <td>{navn}</td>
      </tr>
      <tr>
        <td>Felles barn født før mai 1991</td>
        <td>{fellesFor0591}</td>
      </tr>
      <tr>
        <td>Felles barn født etter mai 1991</td>
        <td>{fellesEtter0591}</td>
      </tr>
    </InfoTable>
  </Column>
</Row>;


Barn.propTypes = {
  fnr: React.PropTypes.string.isRequired,
  navn: React.PropTypes.string.isRequired,
  fellesFor0591: React.PropTypes.string.isRequired,
  fellesEtter0591: React.PropTypes.string.isRequired,
};

export default Barn;

