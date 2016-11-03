package org.perfectable.repositable.configuration;

import org.perfectable.repositable.authorization.Group;
import org.perfectable.repositable.authorization.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

@XmlType(name = "User", propOrder = {"identifier", "username", "password"})
@XmlAccessorType(XmlAccessType.NONE)
public class UserConfiguration {
	@SuppressWarnings("unused")
	@XmlID
	@XmlAttribute(name = "id")
	private String identifier;

	@SuppressWarnings("unused")
	@XmlElement(name = "username", required = true)
	private String username;

	@SuppressWarnings("unused")
	@XmlElement(name = "password", required = true)
	private String password;

	private transient User builtUser;

	public User build() {
		if(builtUser == null) {
			builtUser = User.create(username, password);
		}
		return builtUser;
	}

	public Group appendTo(Group group) {
		return group.join(build());
	}

	@XmlType(name = "UserConfigurationReference", propOrder = {"user"})
	public static class Reference {

		@XmlIDREF
		@XmlAttribute(name = "ref", required = true)
		private UserConfiguration user;

		private Reference() {
			// required for jaxb
		}

		Reference(UserConfiguration user) {
			this.user = user;
		}

		public static class Adapter extends XmlAdapter<Reference, UserConfiguration> {
			@Override
			public UserConfiguration unmarshal(Reference reference) {
				return reference.user;
			}

			@Override
			public Reference marshal(UserConfiguration user) {
				return new Reference(user);
			}
		}
	}
}
