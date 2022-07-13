import { Link } from 'react-router-dom';

import cn from 'classnames';

import Button from 'common/components/Button';
import Icon from 'common/components/Icon';
import Text from 'common/components/Text';

import imageHero from 'assets/images/demo-create.png';

import styles from './styles.module.scss';

function MainPage() {
  return (
    <>
      <div className={cn(styles.container)}>
        <img className="" src={imageHero} alt="웰컴 메시지" />
      </div>
      <div className={cn(styles.container)}>
        <Text className={cn(styles.title)} size={32} weight="bold">
          회고를 통해 팀원과 함께 성장하세요!
        </Text>
        <Text className={cn(styles.subtitle)} size={18} weight="lighter">
          팀원과 공유할 회고 질문을 작성하고 팀원들에게 참여 코드를 공유하면 팀원들의 회고를
          간편하게 모아 볼 수 있습니다
        </Text>
        <div className={cn('button-container horizontal')}>
          <Link to={'/review-forms/create'}>
            <Button type="button" filled>
              <Icon type="outlined" code="maps_ugc" />
              <span>회고 생성</span>
            </Button>
          </Link>

          <Link to={'/review-forms/join'}>
            <Button type="button" filled>
              <Icon code="login" />
              <span>회고 참여</span>
            </Button>
          </Link>
        </div>
      </div>
    </>
  );
}

export default MainPage;