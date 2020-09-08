import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './role.reducer';
import { IRole } from 'app/shared/model/role.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRoleDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RoleDetail = (props: IRoleDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { roleEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          <Translate contentKey="app1App.role.detail.title">Role</Translate> [<b>{roleEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="roleName">
              <Translate contentKey="app1App.role.roleName">Role Name</Translate>
            </span>
          </dt>
          <dd>{roleEntity.roleName}</dd>
          <dt>
            <span id="additional">
              <Translate contentKey="app1App.role.additional">Additional</Translate>
            </span>
          </dt>
          <dd>{roleEntity.additional}</dd>
        </dl>
        <Button tag={Link} to="/role" replace color="info">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/role/${roleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ role }: IRootState) => ({
  roleEntity: role.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RoleDetail);
